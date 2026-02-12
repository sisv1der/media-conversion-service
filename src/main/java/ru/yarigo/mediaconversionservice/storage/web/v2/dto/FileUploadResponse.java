package ru.yarigo.mediaconversionservice.storage.web.v2.dto;

import java.util.UUID;

public record FileUploadResponse(
        UUID jobId,
        String jobStatus
) {}