package ru.yarigo.mediaconverionservice.validation;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.FrameGrabber;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_WARNING;

public interface FfmpegValidator extends Validator {

    List<Consumer<FFmpegFrameGrabber>> steps();

    @Override
    default boolean isValid(Path file) {

        var steps = steps();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.toFile())) {
            for (var step : steps) {
                step.accept(grabber);
            }

            FFmpegLogCallback.setLevel(AV_LOG_WARNING);

            grabber.start();
            boolean formatOk = grabber.getFormat() != null && grabber.getFormat().contains(mediaFormat().name().toLowerCase());

            grabber.stop();
            return formatOk;
        } catch (FrameGrabber.Exception e) {
            return false;
        }
    }
}
