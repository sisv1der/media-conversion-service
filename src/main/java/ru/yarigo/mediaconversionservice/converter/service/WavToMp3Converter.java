package ru.yarigo.mediaconversionservice.converter.service;

import org.bytedeco.javacv.*;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.ConversionKey;
import ru.yarigo.mediaconversionservice.converter.Convertible;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;

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
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath.toFile());
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();

            recorder = new FFmpegFrameRecorder(
                    outputPath.toFile(),
                    grabber.getAudioChannels()
            );

            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioCodecName("libmp3lame");
            recorder.setFormat("mp3");
            recorder.start();

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            grabber.stop();
            grabber.release();
        }
    }
}
