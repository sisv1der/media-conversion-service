package ru.yarigo.mediaconversionservice.conversion.job.consumer;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.conversion.job.service.ConversionService;
import ru.yarigo.mediaconversionservice.conversion.job.service.KeyGenerator;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

@Component
public class PostgresJobConsumer implements JobConsumer {

    private final JobRepository jobRepository;
    private final Executor executor;
    private final StorageService storageService;
    private final ConversionService conversionService;

    public PostgresJobConsumer(
            JobRepository jobRepository,
            @Qualifier("conversionExecutor") Executor executor,
            StorageService storageService,
            ConversionService conversionService) {
        this.jobRepository = jobRepository;
        this.executor = executor;
        this.storageService = storageService;
        this.conversionService = conversionService;
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
        var inputPath = Files.createTempFile(
                "processing-",
                "." + getInputFormat(job.getFilename())
        );
        try (var inputStream = storageService.download(job.getInputS3Key())) {
            Files.copy(inputStream, inputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            markFailed(job);
            Files.deleteIfExists(inputPath);
            throw new RuntimeException(e);
        }
        var outputPath = Files.createTempFile(
                "processing-output-",
                "." + getInputFormat(job.getFilename())
        );

        try {
            conversionService.convert(
                    inputPath,
                    outputPath,
                    getInputFormat(job.getFilename()),
                    getFormat(getExtension("a." + job.getOutputFormat().name().toLowerCase()))
                    //  Тут костыль: из-за того, что существует 2 разных MediaFormat
                    //      мне приходится вот так криво получать нужный (доменный) MediaFormat из JPA-специфичного enum
            );

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

    private MediaFormat getInputFormat(String filename) {
        var extension = getExtension(filename);
        return getFormat(extension);
    }

    private MediaFormat getFormat(String extension) {
        if (extension == null || extension.isEmpty()) {
            return null;
        }

        String ext = extension.toLowerCase().replaceAll("^\\.", "");

        return Arrays.stream(MediaFormat.values())
                .filter(format -> format.getExtension().equals(ext))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid extension: " + ext));
    }

    private static String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        return FilenameUtils.getExtension(filename);
    }

    // TODO: ОЧЕНЬ требуется сделать маппер между доменным и инфраструктурными MediaFormat
    //      Код для конверсии уже второй раз копипастится между классами

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
