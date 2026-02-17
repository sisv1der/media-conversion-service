package ru.yarigo.mediaconversionservice.job.web.dto;

import java.util.UUID;

public record ReadJobStatusResponse(
        UUID jobId,
        String jobStatus
) {
}
