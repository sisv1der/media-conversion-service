package ru.yarigo.mediaconverionservice.validation.impl;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.validation.FfmpegValidator;

import java.util.List;
import java.util.function.Consumer;

public class Mp4Validator implements FfmpegValidator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.MP4;
    }


    @Override
    public List<Consumer<FFmpegFrameGrabber>> steps() {
        return List.of(
                (g) -> g.setPixelFormat(avutil.AV_PIX_FMT_YUV420P)
        );
    }
}
