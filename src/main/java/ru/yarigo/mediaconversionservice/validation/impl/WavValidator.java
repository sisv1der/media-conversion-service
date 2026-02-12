package ru.yarigo.mediaconversionservice.validation.impl;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

@Service
class WavValidator implements Validator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.WAV;
    }
}
