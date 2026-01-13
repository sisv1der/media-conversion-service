package ru.yarigo.mediaconversionservice.signature.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.signature.SignatureValidator;

import java.nio.file.Path;

@Service
class Mp3SignatureValidator implements SignatureValidator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.MP3;
    }

    @Override
    public boolean isValid(Path file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.toFile())) {
            grabber.start();
            grabber.stop();

            return true;
        } catch (FrameGrabber.Exception e) {
            return false;
        }
    }
}
