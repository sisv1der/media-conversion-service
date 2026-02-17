package ru.yarigo.mediaconversionservice.validation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.exception.UnsupportedMediaFormatException;
import ru.yarigo.mediaconversionservice.validation.ValidatorRegistry;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final ValidatorRegistry validatorRegistry;

    public boolean isValid(Path inputPath, MediaFormat requiredFormat) {
        return validatorRegistry.get(requiredFormat)
                .orElseThrow(() -> new UnsupportedMediaFormatException(requiredFormat + " is not supported"))
                .isValid(inputPath);
    }

    public boolean isNotValid(Path inputPath, MediaFormat requiredFormat) {
        return !isValid(inputPath, requiredFormat);
    }
}
