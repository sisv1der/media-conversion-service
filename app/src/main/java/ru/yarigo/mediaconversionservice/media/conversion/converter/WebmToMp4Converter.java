package ru.yarigo.mediaconversionservice.media.conversion.converter;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.media.conversion.ConversionKey;
import ru.yarigo.mediaconversionservice.media.conversion.Convertible;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.media.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconversionservice.media.conversion.engine.RecorderFactory;
import ru.yarigo.mediaconversionservice.media.conversion.exception.ConversionException;

import java.nio.file.Path;

@Slf4j
@Service
class WebmToMp4Converter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.WEBM, MediaFormat.MP4);
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
        } catch (FFmpegFrameRecorder.Exception | FFmpegFrameGrabber.Exception e) {
            log.warn("{} to {} convert failed: input={}, output={}", key().in(), key().out(), inputPath, outputPath, e);
            throw new ConversionException("Conversion failed", e);
        }
    }
}
