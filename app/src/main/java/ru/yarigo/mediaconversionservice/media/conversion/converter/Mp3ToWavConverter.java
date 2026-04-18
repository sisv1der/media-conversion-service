package ru.yarigo.mediaconversionservice.media.conversion.converter;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.media.conversion.ConversionKey;
import ru.yarigo.mediaconversionservice.media.conversion.Convertible;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.media.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconversionservice.media.conversion.engine.RecorderFactory;
import ru.yarigo.mediaconversionservice.media.conversion.exception.ConversionException;

import java.nio.file.Path;

@Slf4j
@Component
public class Mp3ToWavConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.MP3, MediaFormat.WAV);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getAudioChannels()
        );

        convert(inputPath, outputPath, recorderFactory);
    }

    private void convert(Path inputPath, Path outputPath, RecorderFactory recorderFactory) {
        try {
            new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                    .step((g, r) -> r.setAudioChannels(g.getAudioChannels()))
                    .step((g, r) -> r.setSampleRate(g.getSampleRate()))
                    .step(r -> r.setAudioCodecName("pcm_s16le"))
                    .step(r -> r.setFormat("wav"))
                    .convert();
        } catch (FFmpegFrameRecorder.Exception | FFmpegFrameGrabber.Exception e) {
            log.warn("{} to {} convert failed: input={}, output={}", key().in(), key().out(), inputPath, outputPath, e);
            throw new ConversionException("Conversion failed", e);
        }
    }
}
