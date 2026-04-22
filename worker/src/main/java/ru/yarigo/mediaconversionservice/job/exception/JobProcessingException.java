package ru.yarigo.mediaconversionservice.job.exception;

public class JobProcessingException extends RuntimeException {
    public JobProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
