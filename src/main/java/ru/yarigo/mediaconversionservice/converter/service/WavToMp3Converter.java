package ru.yarigo.mediaconversionservice.converter.service;

import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.*;

import java.nio.file.Path;

@Service
class WavToMp3Converter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.WAV, MediaFormat.MP3);
    }

    @Override
    public void convert(
            Path inputPath,
            Path outputPath
    ) throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        RecorderFactory recorderFactory = (_, g) -> new FFmpegFrameRecorder(
                outputPath.toFile(),
                g.getAudioChannels()
        );

        new FfmpegPipeline(inputPath, outputPath, recorderFactory)
                .step((g, r) -> r.setAudioChannels(g.getAudioChannels()))
                .step((g, r) -> r.setSampleRate(g.getSampleRate()))
                .step(r -> r.setAudioCodecName("libmp3lame"))
                .step(r -> r.setFormat("mp3"))
                .convert();
    }
}
