package ru.yarigo.mediaconversionservice.conversion.job.web.dto;

import java.util.UUID;

public record ReadJobStatusResponse(
        UUID jobId,
        String jobStatus
) {
}
