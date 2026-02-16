package ru.yarigo.mediaconversionservice.conversion.job.consumer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.conversion.job.service.ConversionService;
import ru.yarigo.mediaconversionservice.conversion.job.service.KeyGenerator;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.Executor;

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
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           });
        }
    }

    private void processJob(JobEntity job) throws IOException {
        var inputFormat = mediaFormatMapper.map(job.getInputFormat());
        var inputPath = Files.createTempFile(
                "processing-",
                "." + inputFormat.getExtension()
        );
        try (var inputStream = storageService.download(job.getInputS3Key())) {
            Files.copy(inputStream, inputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            markFailed(job);
            Files.deleteIfExists(inputPath);
            throw new RuntimeException(e);
        }
        var outputFormat = mediaFormatMapper.map(job.getOutputFormat());
        var outputPath = Files.createTempFile(
                "processing-output-",
                "." + outputFormat.getExtension()
        );

        try {
            conversionService.convert(inputPath, outputPath, inputFormat, outputFormat);

            var outputKey = KeyGenerator.outputKey(
                    job.getId(),
                    job.getOutputFormat().name()
            );

            storageService.upload(outputKey, outputPath);
            job.setOutputS3Key(outputKey);

            markDone(job);
        } catch (Exception ex) {
            markFailed(job);
        } finally {
            storageService.delete(job.getInputS3Key());
            Files.deleteIfExists(inputPath);
            Files.deleteIfExists(outputPath);
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
