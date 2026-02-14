package ru.yarigo.mediaconversionservice.conversion.job.web.dto;

import java.util.UUID;

public record CreateJobResponse(
        UUID jobId,
        String jobStatus
) {}
