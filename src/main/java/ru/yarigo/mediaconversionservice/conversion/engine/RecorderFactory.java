package ru.yarigo.mediaconversionservice.conversion.engine;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.nio.file.Path;

@FunctionalInterface
public interface RecorderFactory {
    FFmpegFrameRecorder create(Path output, FFmpegFrameGrabber grabber);
}
