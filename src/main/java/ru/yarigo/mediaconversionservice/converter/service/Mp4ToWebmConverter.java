package ru.yarigo.mediaconversionservice.converter.service;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.converter.ConversionKey;
import ru.yarigo.mediaconversionservice.converter.Converter;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import java.nio.file.Path;

@Component
public class Mp4ToWebmConverter implements Converter {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.MP4, MediaFormat.WEBM);
    }

    @Override
    public void convert(
            Path inputPath,
            Path outputPath
    ) throws FrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath.toFile());
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();

            recorder = new FFmpegFrameRecorder(
                    outputPath.toFile(),
                    grabber.getImageWidth(),
                    grabber.getImageHeight()
            );

            recorder.setFormat("webm");

            recorder.setVideoCodecName("libvpx-vp9");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

            recorder.setVideoOption("crf", "32");
            recorder.setVideoOption("b:v", "0");
            recorder.setVideoOption("deadline", "good");

            if (grabber.hasAudio()) {
                recorder.setAudioCodecName("libopus");
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setSampleRate(48000);
            }

            recorder.start();

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                recorder.record(frame);
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
