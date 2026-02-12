package ru.yarigo.mediaconversionservice.validation;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import java.nio.file.Path;

public interface Validator {

    MediaFormat mediaFormat();

    default boolean isValid(Path file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.toFile())) {
            grabber.start();
            boolean formatOk = grabber.getFormat() != null && grabber.getFormat().contains(mediaFormat().name());

            grabber.stop();
            return formatOk;
        } catch (FrameGrabber.Exception e) {
            return false;
        }
    }

    default boolean isNotValid(Path file) {
        return !isValid(file);
    }
}
