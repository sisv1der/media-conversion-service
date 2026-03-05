package ru.yarigo.mediaconversionservice.conversion.exception;

public class ConversionException extends RuntimeException {
    public ConversionException(String message) {
        super(message);
    }
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}
