package ru.yarigo.mediaconversionservice.job.exception;

public class JobClaimingException extends RuntimeException {
    public JobClaimingException(final String message) {
        super(message);
    }
    public JobClaimingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
