package ru.yarigo.mediaconversionservice.job.web.dto;

import ru.yarigo.mediaconversionservice.job.model.JobStatus;

import java.util.UUID;

public record ReadJobStatusRequest(
        UUID jobId,
        JobStatus JobStatus
) {}