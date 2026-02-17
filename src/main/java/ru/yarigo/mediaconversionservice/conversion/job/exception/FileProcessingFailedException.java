package ru.yarigo.mediaconversionservice.conversion.job.exception;

public class FileProcessingFailedException extends RuntimeException {
    public FileProcessingFailedException(String message) {
        super(message);
    }
    public FileProcessingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
