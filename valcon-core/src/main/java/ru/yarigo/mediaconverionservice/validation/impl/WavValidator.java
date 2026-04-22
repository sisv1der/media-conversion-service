package ru.yarigo.mediaconverionservice.validation.impl;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.validation.FfmpegValidator;

import java.util.List;
import java.util.function.Consumer;

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
