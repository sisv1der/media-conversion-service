package ru.yarigo.mediaconversionservice.signature;

import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import java.nio.file.Path;

public interface SignatureValidator {

    MediaFormat mediaFormat();

    boolean isValid(Path file);

    default boolean isNotValid(Path file) {
        return !isValid(file);
    }
}
