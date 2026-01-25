package ru.yarigo.mediaconversionservice.validation.service;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

import java.nio.file.Path;

@Service
class Mp3Validator implements Validator {

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.MP3;
    }

    @Override
    public boolean isValid(Path file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.toFile())) {
            grabber.start();
            boolean formatOk = grabber.getFormat() != null && grabber.getFormat().contains("mp3");

            grabber.stop();
            return formatOk;
        } catch (FrameGrabber.Exception e) {
            return false;
        }
    }
}
