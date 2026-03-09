package ru.yarigo.mediaconversionservice.validation.impl;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.FfmpegValidator;

import java.util.List;
import java.util.function.Consumer;

@Service
public class WebmValidator implements FfmpegValidator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.WEBM;
    }

    @Override
    public List<Consumer<FFmpegFrameGrabber>> steps() {
        return List.of();
    }
}
