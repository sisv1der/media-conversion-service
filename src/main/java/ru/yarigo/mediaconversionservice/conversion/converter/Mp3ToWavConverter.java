package ru.yarigo.mediaconversionservice.conversion.converter;

import org.bytedeco.javacv.*;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.*;
import ru.yarigo.mediaconversionservice.conversion.engine.FfmpegPipeline;
import ru.yarigo.mediaconversionservice.conversion.engine.RecorderFactory;

import java.nio.file.Path;

@Component
public class Mp3ToWavConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.MP3, MediaFormat.WAV);
    }

    @Override
    public void convert(
            Path inputPath,
            Path outputPath
    ) throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getAudioChannels()
        );

        new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                    .step((g, r) -> r.setAudioChannels(g.getAudioChannels()))
                    .step((g, r) -> r.setSampleRate(g.getSampleRate()))
                    .step(r -> r.setAudioCodecName("pcm_s16le"))
                    .step(r -> r.setFormat("wav"))
                    .convert();
    }
}
