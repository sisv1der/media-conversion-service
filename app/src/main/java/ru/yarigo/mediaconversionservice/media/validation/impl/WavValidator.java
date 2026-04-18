package ru.yarigo.mediaconversionservice.media.validation.impl;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.media.validation.FfmpegValidator;

import java.util.List;
import java.util.function.Consumer;

@Service
public class WavValidator implements FfmpegValidator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.WAV;
    }

    @Override
    public List<Consumer<FFmpegFrameGrabber>> steps() {
        return List.of();
    }
}
