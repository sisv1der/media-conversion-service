package ru.yarigo.mediaconversionservice.validation.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

@Service
class WavValidator implements Validator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.WAV;
    }
}
