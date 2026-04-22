package ru.yarigo.mediaconverionservice.conversion.exception;

public class UnsupportedMediaFormatException extends RuntimeException {
    public UnsupportedMediaFormatException(String message) {
        super(message);
    }
}
