package ru.yarigo.mediaconversionservice.validation;

import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import java.nio.file.Path;

public interface Validator {

    MediaFormat mediaFormat();

    boolean isValid(Path file);

    default boolean isNotValid(Path file) {
        return !isValid(file);
    }
}
