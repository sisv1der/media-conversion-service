package ru.yarigo.mediaconversionservice.job.web.exception;

public class TooEarlyException extends RuntimeException {
    public TooEarlyException(String message) {
        super(message);
    }
    public TooEarlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
