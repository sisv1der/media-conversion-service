package ru.yarigo.mediaconversionservice.converter.service;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.*;

import java.nio.file.Path;

@Service
class WebmToMp4Converter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.WEBM, MediaFormat.MP4);
    }

    @Override
    public void convert(
            Path inputPath,
            Path outputPath
    ) throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getImageWidth(),
                g.getImageHeight()
        );

        new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                .step(r -> r.setVideoCodecName("libx264"))
                .step(r -> r.setFormat("mp4"))
                .step(r -> r.setPixelFormat(avutil.AV_PIX_FMT_YUV420P))
                .step(r -> r.setVideoOption("crf", "22"))
                .step((g, r) -> r.setFrameRate(g.getFrameRate()))
                .step((g, r) -> {
                    if (g.hasAudio()) {
                        r.setAudioCodecName("aac");
                        r.setAudioChannels(g.getAudioChannels());
                        r.setSampleRate(48000);
                    }
                })
                .convert();
    }
}
