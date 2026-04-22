package ru.yarigo.mediaconversionservice.kafka;

import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.job.model.MediaFormat;

import java.util.UUID;

public record JobEvent(
        UUID jobId,
        String inputS3Key,
        String outputS3Key,
        MediaFormat inputFormat,
        MediaFormat outputFormat,
        JobStatus status,
        String errorMessage
) {
}
