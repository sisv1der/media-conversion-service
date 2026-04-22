package ru.yarigo.mediaconversionservice.job.model;

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

    public JobEvent withOutputS3Key(String outputS3Key) {
        return new JobEvent(
                this.jobId,
                this.inputS3Key,
                outputS3Key,
                this.inputFormat,
                this.outputFormat,
                this.status,
                this.errorMessage
        );
    }

    public JobEvent withStatus(JobStatus status) {
        return new JobEvent(
                this.jobId,
                this.inputS3Key,
                this.outputS3Key,
                this.inputFormat,
                this.outputFormat,
                status,
                this.errorMessage
        );
    }

    public JobEvent withErrorMessage(String errorMessage) {
        return new JobEvent(
                this.jobId,
                this.inputS3Key,
                this.outputS3Key,
                this.inputFormat,
                this.outputFormat,
                this.status,
                errorMessage
        );
    }
}
