package ru.yarigo.mediaconversionservice.job.processor;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.media.conversion.exception.ConversionException;
import ru.yarigo.mediaconversionservice.media.conversion.service.ConversionService;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;
import ru.yarigo.mediaconversionservice.workspace.InputStreamWorkspace;
import ru.yarigo.mediaconversionservice.workspace.WorkspaceException;

import static ru.yarigo.mediaconversionservice.storage.util.KeyGenerator.outputKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProcessorImpl implements JobProcessor<JobEntity> {

    private final StorageService storageService;
    private final ConversionService conversionService;
    private final JobRepository jobRepository;
    private final MediaFormatMapper mediaFormatMapper;
    private final InputStreamWorkspace workspace;

    @Transactional
    public void process(JobEntity job) {
        var inputFormat = mediaFormatMapper.map(job.getInputFormat());
        var outputFormat = mediaFormatMapper.map(job.getOutputFormat());
        try (var inputStream = storageService.download(job.getInputS3Key())) {
            markDone(
                    workspace.execute(
                            inputStream,
                            "processing-",
                            "." + inputFormat.getExtension(),
                            input -> {
                                var output = createTempFile(
                                        "processing-output-",
                                        "." + outputFormat.getExtension(),
                                        job.getId()
                                );

                                convert(input, output, inputFormat, outputFormat);

                                var outputKey = outputKey(
                                        job.getId(),
                                        job.getOutputFormat().name()
                                );
                                upload(outputKey, output);
                                job.setOutputS3Key(outputKey);

                                deleteFile(output);

                                return job;
                    })
            );
        } catch (IOException | WorkspaceException e ) {
            markFailed(job, e);
            throw new JobProcessingException("Error while job processing: " + job.getId(), e);
        }
    }

    private void upload(String outputKey, Path outputPath) {
        try {
            storageService.upload(outputKey, outputPath);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error uploading output file " + outputKey, e);
        }
    }

    private void markFailed(JobEntity jobEntity, Throwable cause) {
        jobEntity.setStatus(JobStatus.FAILED);
        jobEntity.setErrorMessage(cause.getMessage());
        jobRepository.save(jobEntity);
    }

    private void markDone(JobEntity jobEntity) {
        jobEntity.setStatus(JobStatus.DONE);
        jobRepository.save(jobEntity);
    }

    private Path createTempFile(String prefix, String suffix, UUID jobId) {
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            log.warn("Failed to create temporary file {} for job {}", prefix, jobId, e);
            throw new JobProcessingException("Error creating temporary file for job " + jobId, e);
        }
    }

    private void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException _) {}
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
}
