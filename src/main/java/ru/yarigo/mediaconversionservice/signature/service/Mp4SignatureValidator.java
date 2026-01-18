package ru.yarigo.mediaconversionservice.signature.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.signature.SignatureValidator;

import java.nio.file.Path;

@Service
class Mp4SignatureValidator implements SignatureValidator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.MP4;
    }

    @Override
    public boolean isValid(Path file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.toFile())) {
            grabber.start();
            boolean hasVideo = grabber.getLengthInFrames() > 0 || grabber.getVideoCodec() != 0;
            boolean formatOk = grabber.getFormat() != null && grabber.getFormat().contains("mp4");

            grabber.stop();
            return hasVideo && formatOk;
        } catch (FrameGrabber.Exception e) {
            return false;
        }
    }
}
