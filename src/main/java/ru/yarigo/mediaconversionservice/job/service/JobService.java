package ru.yarigo.mediaconversionservice.job.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.job.exception.FileProcessingFailedException;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.exception.ValidationException;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.job.web.dto.CreateJobResponse;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.job.web.dto.FileResource;
import ru.yarigo.mediaconversionservice.job.web.dto.ReadJobStatusResponse;
import ru.yarigo.mediaconversionservice.job.web.exception.TooEarlyException;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;
import ru.yarigo.mediaconversionservice.validation.service.ValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ValidationService validationService;
    private final StorageService storageService;
    private final MediaFormatMapper mediaFormatMapper;

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
            ru.yarigo.mediaconversionservice.conversion.MediaFormat outputFormat
    ) {
        var inputFormat = MediaFormat.getMediaFormat(file.getOriginalFilename());
        var inputPath = createTempFile(
                "input-",
                "." + inputFormat.getExtension()
        );
        try {
            validateFile(file);
            file.transferTo(inputPath);
            validateBeforeConversion(inputPath, inputFormat);

            var job = save(inputPath, file.getOriginalFilename(), inputFormat, outputFormat);

            return new CreateJobResponse(job.getId(), JobStatus.PENDING.name());
        } catch (IOException | ValidationException e) {
            throw new JobProcessingException("Error converting input file " + inputPath, e);
        } finally {
            deleteFileIfExists(inputPath);
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

    private void validateBeforeConversion(Path inputPath, MediaFormat requiredFormat) {
        if (validationService.isNotValid(inputPath, requiredFormat)) {
            throw new ValidationException("Provided file is not valid");
        }
    }

    private JobEntity save(
            Path inputPath,
            String filename,
            MediaFormat inputFormat,
            MediaFormat outputFormat
    ) {
        var jobId = UUID.randomUUID();
        var inputKey = KeyGenerator.inputKey(jobId, filename);
        var job = JobEntity.builder()
                .id(jobId)
                .filename(filename)
                .inputS3Key(inputKey)
                .inputFormat(mediaFormatMapper.map(inputFormat))
                .outputFormat(mediaFormatMapper.map(outputFormat))
                .build();

        upload(inputKey, inputPath);
        saveToDb(job);

        return job;
    }

    private void saveToDb(JobEntity job) {
        try {
            jobRepository.save(job);
        } catch (Exception e) {
            log.error("Error while saving input file into DB: s3-key={}", job.getInputS3Key(), e);
            try {
                delete(job.getInputS3Key());
            } catch (Exception deleteException) {
                e.addSuppressed(deleteException);
            }
            throw new JobProcessingException("Error while saving job", e);
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

    private Path createTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            log.warn("Failed to create temporary file {}", prefix, e);
            throw new JobProcessingException("Error creating temporary file", e);
        }
    }

    private void deleteFileIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temporary file {}", path, e);
        }
    }
}
