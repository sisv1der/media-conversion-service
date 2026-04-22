package ru.yarigo.mediaconverionservice.conversion.converter;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yarigo.mediaconverionservice.conversion.ConversionKey;
import ru.yarigo.mediaconverionservice.conversion.Convertible;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconverionservice.conversion.engine.RecorderFactory;
import ru.yarigo.mediaconverionservice.conversion.exception.ConversionException;

import java.nio.file.Path;

public class Mp4ToWebmConverter implements Convertible {

    private final Logger logger = LoggerFactory.getLogger(Mp4ToWebmConverter.class);

    public ConversionKey key() {
        return new ConversionKey(MediaFormat.MP4, MediaFormat.WEBM);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getImageWidth(),
                g.getImageHeight()
        );

        convert(inputPath, outputPath, recorderFactory);
    }

    private void convert(Path inputPath, Path outputPath, RecorderFactory recorderFactory) {
        try {
            new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                    .grabberStep(g -> g.setPixelFormat(avutil.AV_PIX_FMT_YUV420P))
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
        } catch (FFmpegFrameRecorder.Exception | FFmpegFrameGrabber.Exception e) {
            logger.debug("{} to {} convert failed: input={}, output={}", key().in(), key().out(), inputPath, outputPath, e);
            throw new ConversionException("Conversion failed", e);
        }
    }
}
