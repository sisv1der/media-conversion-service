package ru.yarigo.mediaconversionservice.job.event;

import ru.yarigo.mediaconversionservice.job.model.JobStatus;

import java.util.UUID;

public record JobStatusUpdatedEvent(UUID jobId, JobStatus jobStatus, String errorMessage) {

}
