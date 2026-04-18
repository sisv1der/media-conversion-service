package ru.yarigo.mediaconversionservice.media.validation;

import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;

import java.nio.file.Path;

public interface Validator {

    MediaFormat mediaFormat();

    boolean isValid(Path file);
}
