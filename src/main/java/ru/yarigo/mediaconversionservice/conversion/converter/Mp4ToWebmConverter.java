package ru.yarigo.mediaconversionservice.conversion.converter;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.*;
import ru.yarigo.mediaconversionservice.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconversionservice.conversion.engine.RecorderFactory;

import java.nio.file.Path;

@Component
public class Mp4ToWebmConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.MP4, MediaFormat.WEBM);
    }

    @Override
    public void convert(
            Path inputPath,
            Path outputPath
    ) throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getImageWidth(),
                g.getImageHeight()
        );

        new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                .step(r -> r.setVideoCodecName("libvpx-vp9"))
                .step(r -> r.setFormat("webm"))
                .step(r -> r.setPixelFormat(avutil.AV_PIX_FMT_YUV420P))
                .step(r -> r.setVideoOption("crf", "32"))
                .step(r -> r.setVideoOption("b:v", "0"))
                .step(r -> r.setVideoOption("deadline", "good"))
                .step((g, r) -> r.setFrameRate(g.getFrameRate()))
                .step((g, r) -> {
                    if (g.hasAudio()) {
                        r.setAudioCodecName("libopus");
                        r.setAudioChannels(g.getAudioChannels());
                        r.setSampleRate(48000);
                    }
                })
                .convert();
    }
}
