package ru.yarigo.mediaconversionservice.validation;

import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import java.nio.file.Path;

public interface Validator {

    MediaFormat mediaFormat();

    boolean isValid(Path file);
}
