package ru.yarigo.mediaconversionservice.validation.impl;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.FfmpegValidator;

import java.util.List;
import java.util.function.Consumer;

@Service
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
