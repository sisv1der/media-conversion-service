package ru.yarigo.mediaconversionservice.media.conversion.exception;

public class UnsupportedMediaFormatException extends RuntimeException {
    public UnsupportedMediaFormatException(String message) {
        super(message);
    }
}
