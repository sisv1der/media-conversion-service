package ru.yarigo.mediaconversionservice.media.validation.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.exception.UnsupportedMediaFormatException;
import ru.yarigo.mediaconversionservice.validation.ValidatorRegistry;

import java.nio.file.Path;

@Service
public class ValidationService {

    private final ValidatorRegistry validatorRegistry;

    public ValidationService(ValidatorRegistry validatorRegistry) {
        this.validatorRegistry = validatorRegistry;
    }

    public boolean isValid(Path inputPath, MediaFormat requiredFormat) {
        return validatorRegistry.get(requiredFormat)
                .orElseThrow(() -> new UnsupportedMediaFormatException(requiredFormat + " is not supported"))
                .isValid(inputPath);
    }

    public boolean isNotValid(Path inputPath, MediaFormat requiredFormat) {
        return !isValid(inputPath, requiredFormat);
    }
}
