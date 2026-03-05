package ru.yarigo.mediaconversionservice.conversion.converter;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.*;
import ru.yarigo.mediaconversionservice.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconversionservice.conversion.engine.RecorderFactory;
import ru.yarigo.mediaconversionservice.conversion.exception.ConversionException;

import java.nio.file.Path;

@Slf4j
@Service
class WavToMp3Converter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.WAV, MediaFormat.MP3);
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
                    .step(r -> r.setAudioCodecName("libmp3lame"))
                    .step(r -> r.setFormat("mp3"))
                    .convert();
        } catch (FFmpegFrameRecorder.Exception | FFmpegFrameGrabber.Exception e) {
            log.warn("{} to {} convert failed: input={}, output={}", key().in(), key().out(), inputPath, outputPath, e);
            throw new ConversionException("Conversion failed", e);
        }
    }
}
