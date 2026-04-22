package ru.yarigo.mediaconversionservice.job.exception;

public class JobAlreadyProcessingException extends RuntimeException {
    public JobAlreadyProcessingException(String message) {
        super(message);
    }
}
