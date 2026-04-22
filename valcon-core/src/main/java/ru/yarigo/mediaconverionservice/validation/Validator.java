package ru.yarigo.mediaconverionservice.validation;

import ru.yarigo.mediaconverionservice.conversion.MediaFormat;

import java.nio.file.Path;

public interface Validator {

    MediaFormat mediaFormat();

    boolean isValid(Path file);
}
