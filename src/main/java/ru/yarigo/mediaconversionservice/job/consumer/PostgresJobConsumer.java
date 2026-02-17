package ru.yarigo.mediaconversionservice.job.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.conversion.exception.ConversionException;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.conversion.service.ConversionService;
import ru.yarigo.mediaconversionservice.job.service.KeyGenerator;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class PostgresJobConsumer implements JobConsumer {

    private final JobRepository jobRepository;
    private final Executor executor;
    private final StorageService storageService;
    private final ConversionService conversionService;
    private final MediaFormatMapper mediaFormatMapper;

    public PostgresJobConsumer(
            JobRepository jobRepository,
            @Qualifier("conversionExecutor") Executor executor,
            StorageService storageService,
            ConversionService conversionService,
            MediaFormatMapper mediaFormatMapper) {
        this.jobRepository = jobRepository;
        this.executor = executor;
        this.storageService = storageService;
        this.conversionService = conversionService;
        this.mediaFormatMapper = mediaFormatMapper;
    }

    @Scheduled(fixedRate = 5000)
    @Override
    public void process() {
        int JOB_LIMIT = 10;
        var jobs = pickJobsToProcess(JOB_LIMIT);

        for (JobEntity job : jobs) {
           executor.execute(() -> {
               try {
                   processJob(job);
               } catch (JobProcessingException e) {
                   throw new JobProcessingException("Error processing job " + job.getId(), e);
               }
           });
        }
    }

    private void processJob(JobEntity job) {
        var inputFormat = mediaFormatMapper.map(job.getInputFormat());
        Path inputPath = createTempFile(
                "processing-",
                "." + inputFormat.getExtension(),
                job.getId()
        );

        try (var inputStream = download(job.getInputS3Key())) {
            Files.copy(inputStream, inputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            markFailed(job);
            deleteFileIfExists(inputPath, job.getId());
            throw new JobProcessingException("Error downloading input file " + job.getInputS3Key(), e);
        }

        var outputFormat = mediaFormatMapper.map(job.getOutputFormat());
        Path outputPath = createTempFile(
                "processing-output-",
                "." + outputFormat.getExtension(),
                job.getId()
        );

        try {
            convert(inputPath, outputPath, inputFormat, outputFormat);

            var outputKey = KeyGenerator.outputKey(
                    job.getId(),
                    job.getOutputFormat().name()
            );
            upload(outputKey, outputPath);

            job.setOutputS3Key(outputKey);
            markDone(job);
        } catch (Exception ex) {
            markFailed(job);
            log.warn("Error converting input file {} to {} from {} to {}",
                    inputPath,
                    outputPath,
                    job.getInputFormat(),
                    job.getOutputFormat(),
                    ex
            );
            throw new JobProcessingException("Error converting job " + job.getId(), ex);
        } finally {
            storageService.delete(job.getInputS3Key());
            deleteFileIfExists(inputPath, job.getId());
            deleteFileIfExists(outputPath, job.getId());
        }
    }

    private InputStream download(String key) {
        try {
            return storageService.download(key);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error retrieving input file " + key, e);
        }
    }

    private void upload(String outputKey, Path outputPath) {
        try {
            storageService.upload(outputKey, outputPath);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error uploading output file " + outputKey, e);
        }
    }

    private void convert(
            Path inputPath,
            Path outputPath,
            MediaFormat inputFormat,
            MediaFormat outputFormat
    ) {
        try {
            conversionService.convert(inputPath, outputPath, inputFormat, outputFormat);
        } catch (ConversionException e) {
            throw new JobProcessingException("Error converting input file " + inputPath, e);
        }
    }

    private Path createTempFile(String prefix, String suffix, UUID jobId) {
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            log.warn("Failed to create temporary file {} for job {}", prefix, jobId, e);
            throw new JobProcessingException("Error creating temporary file for job " + jobId, e);
        }
    }

    private void deleteFileIfExists(Path file, UUID jobId) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete temporary file {} for job {}", file, jobId, e);
        }
    }

    private void markFailed(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.FAILED);
        jobRepository.save(jobEntity);
    }

    protected List<JobEntity> pickJobsToProcess(int limit) {
        return jobRepository.findByStatus(limit);
    }

    private void markDone(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.DONE);
        jobRepository.save(jobEntity);
    }
}
