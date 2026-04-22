package ru.yarigo.mediaconversionservice.job.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.exception.ConversionException;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.kafka.JobEvent;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.media.conversion.service.ConversionService;
import ru.yarigo.mediaconversionservice.service.StorageService;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;
import ru.yarigo.mediaconversionservice.workspace.InputStreamWorkspace;
import ru.yarigo.mediaconversionservice.workspace.WorkspaceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static ru.yarigo.mediaconversionservice.storage.util.KeyGenerator.outputKey;

@Service
public class JobProcessorImpl implements JobProcessor<JobEvent> {

    private final Logger logger = LoggerFactory.getLogger(JobProcessorImpl.class);

    private final StorageService storageService;
    private final ConversionService conversionService;
    private final MediaFormatMapper mediaFormatMapper;
    private final InputStreamWorkspace workspace;

    public JobProcessorImpl(StorageService storageService, ConversionService conversionService, MediaFormatMapper mediaFormatMapper, InputStreamWorkspace workspace) {
        this.storageService = storageService;
        this.conversionService = conversionService;
        this.mediaFormatMapper = mediaFormatMapper;
        this.workspace = workspace;
    }

    public JobEvent process(JobEvent job) {
        var inputFormat = mediaFormatMapper.map(job.inputFormat());
        var outputFormat = mediaFormatMapper.map(job.outputFormat());
        try (var inputStream = storageService.download(job.inputS3Key())) {
            return markDone(
                    workspace.execute(
                            inputStream,
                            "processing-",
                            "." + inputFormat.getExtension(),
                            input -> {
                                var output = createTempFile(
                                        "processing-output-",
                                        "." + outputFormat.getExtension(),
                                        job.jobId()
                                );

                                convert(input, output, inputFormat, outputFormat);

                                var outputKey = outputKey(
                                        job.jobId(),
                                        job.outputFormat().name()
                                );
                                upload(outputKey, output);

                                deleteFile(output);

                                return job.withOutputS3Key(outputKey);
                    })
            );
        } catch (IOException | WorkspaceException e ) {
            logger.debug("Error while job processing: {}", job.jobId(), e);
            return markFailed(job, e);
        }
    }

    private void upload(String outputKey, Path outputPath) {
        try {
            storageService.upload(outputKey, outputPath);
        } catch (S3StorageException e) {
            throw new JobProcessingException("Error uploading output file " + outputKey, e);
        }
    }

    private JobEvent markFailed(JobEvent job, Throwable cause) {
        return job
                .withStatus(JobStatus.FAILED)
                .withErrorMessage(cause.getMessage());
    }

    private JobEvent markDone(JobEvent job) {
        return job.withStatus(JobStatus.DONE);
    }

    private Path createTempFile(String prefix, String suffix, UUID jobId) {
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            logger.debug("Failed to create temporary file {} for job {}", prefix, jobId, e);
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
