package ru.yarigo.mediaconverionservice.conversion.converter;

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

public class WavToMp3Converter implements Convertible {

    private final Logger logger = LoggerFactory.getLogger(WavToMp3Converter.class);

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
            logger.debug("{} to {} convert failed: input={}, output={}", key().in(), key().out(), inputPath, outputPath, e);
            throw new ConversionException("Conversion failed", e);
        }
    }
}
