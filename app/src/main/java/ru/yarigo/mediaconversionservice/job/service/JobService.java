package ru.yarigo.mediaconversionservice.job.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.job.JobMapper;
import ru.yarigo.mediaconversionservice.job.event.JobStatusUpdatedEvent;
import ru.yarigo.mediaconversionservice.job.exception.FileProcessingFailedException;
import ru.yarigo.mediaconversionservice.job.exception.JobAlreadyProcessingException;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.exception.ValidationException;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.kafka.JobEvent;
import ru.yarigo.mediaconversionservice.kafka.producer.KafkaJobProducer;
import ru.yarigo.mediaconversionservice.job.web.dto.*;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.job.web.exception.TooEarlyException;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;
import ru.yarigo.mediaconversionservice.workspace.FileWorkspace;

import static ru.yarigo.mediaconversionservice.storage.util.KeyGenerator.inputKey;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final StorageService storageService;
    private final JobMapper jobMapper;
    private final FileWorkspace<MultipartFile> workspace;
    private final MediaFormatMapper mediaFormatMapper;
    private final KafkaJobProducer kafkaJobProducer;
    private final ApplicationEventPublisher eventPublisher;

    public FileResource getFileByJobId(UUID jobId) {
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job " + jobId + " not found"));

        return switch (job.getStatus()) {
            case JobStatus.DONE -> new FileResource(
                   new InputStreamResource(download(job.getOutputS3Key())),
                   mediaFormatMapper.map(job.getOutputFormat())
            );
            case JobStatus.FAILED -> throw new FileProcessingFailedException("Job " +  jobId + " failed");
            case JobStatus.PROCESSING, JobStatus.PENDING -> throw new TooEarlyException("Job " + jobId + " has not been done yet");
        };
    }

    public ReadJobStatusResponse getById(UUID jobId) {
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job " + jobId + " not found"));

        return new ReadJobStatusResponse(job.getId(), job.getStatus().name());
    }

    @Transactional
    public CreateJobResponse create(
            MultipartFile file,
            MediaFormat outputFormat
    ) {
        var inputFormat = MediaFormat.getMediaFormat(file.getOriginalFilename());
        var job = workspace.execute(
                file,
                "input-",
                "." + inputFormat.getExtension(),
                inputPath -> {

                    validateFile(file);

                    var jobId = UUID.randomUUID();
                    var inputKey = inputKey(jobId, file.getOriginalFilename());
                    var jobEntity = JobEntity.builder()
                            .id(jobId)
                            .filename(file.getOriginalFilename())
                            .inputS3Key(inputKey)
                            .inputFormat(mediaFormatMapper.map(inputFormat))
                            .outputFormat(mediaFormatMapper.map(outputFormat))
                            .build();

                    return save(inputPath, jobEntity);
        });

        return new CreateJobResponse(job.getId(), JobStatus.PENDING.name());
    }

    public ReadBatchJobStatusResponse getByIds(List<UUID> ids) {
        var jobs = jobRepository.findByIdIn(ids);

        return new ReadBatchJobStatusResponse(jobs.stream()
                .map(jobMapper::map)
                .toList());
    }

    @Transactional
    public int updateJobStatus(UUID jobId, JobStatus status, JobEvent event) {
        int updated = jobRepository.updateJobStatusByIdAndStatus(jobId, status, event.status());

        if (updated != 0) {
            eventPublisher.publishEvent(new JobStatusUpdatedEvent(jobId, event.status(), event.errorMessage()));
        }

        return updated;
    }

    @Transactional
    public void claimJob(UUID jobId) {
        int updated = jobRepository.updateJobStatusByIdAndStatus(jobId, JobStatus.PENDING, JobStatus.PROCESSING);

        if (updated != 0) {
            eventPublisher.publishEvent(new JobStatusUpdatedEvent(jobId, JobStatus.PROCESSING, ""));
        } else {
            throw new JobAlreadyProcessingException("Job " + jobId + " already claimed");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("File " + file.getOriginalFilename() + " is empty");
        }
        long SIZE_LIMIT = 50L * 1024L * 1024L; // 50MB
        if (file.getSize() > SIZE_LIMIT) {
            throw new ValidationException("File " + file.getOriginalFilename() + " is too large");
        }
    }

    private JobEntity save(
            Path inputPath,
            JobEntity job
    ) {
        upload(job.getInputS3Key(), inputPath);
        saveToDb(job);
        kafkaJobProducer.produce(jobMapper.toEvent(job));
        return job;
    }

    private void saveToDb(JobEntity job) {
        try {
            jobRepository.save(job);
        } catch (Exception e) {
            log.error("Error while saving input file into DB: s3-key={}", job.getInputS3Key(), e);
            safeDeleteFromS3(job.getInputS3Key(), e);
            throw new JobProcessingException("Error while saving job", e);
        }
    }

    private void safeDeleteFromS3(String s3Key, Exception originalException) {
        try {
            delete(s3Key);
        } catch (Exception deleteException) {
            originalException.addSuppressed(deleteException);
        }
    }

    private void delete(String key) {
        try {
            storageService.delete(key);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error while deleting file: " + key, e);
        }
    }

    private InputStream download(String key) {
        try {
            return storageService.download(key);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error retrieving input file " + key, e);
        }
    }

    private void upload(String key, Path path) {
        try {
            storageService.upload(key, path);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error uploading output file " + key, e);
        }
    }
}
