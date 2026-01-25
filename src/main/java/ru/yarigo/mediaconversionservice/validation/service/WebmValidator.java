package ru.yarigo.mediaconversionservice.validation.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

@Service
class WebmValidator implements Validator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.WEBM;
    }
}
