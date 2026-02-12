package ru.yarigo.mediaconversionservice.conversion.engine;

import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class FfmpegPipeline {
    private final Path inputPath;
    private final Path outputPath;
    private final RecorderFactory recorderFactory;
    private final List<BiConsumer<FFmpegFrameGrabber, FFmpegFrameRecorder>> steps = new ArrayList<>();

    public FfmpegPipeline step(Consumer<FFmpegFrameRecorder> step) {
        steps.add((_, r) -> step.accept(r));
        return this;
    }

    public FfmpegPipeline step(BiConsumer<FFmpegFrameGrabber, FFmpegFrameRecorder> step) {
        steps.add(step);
        return this;
    }

    public void convert() throws FFmpegFrameRecorder.Exception, FFmpegFrameGrabber.Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath.toFile());
        FFmpegFrameRecorder recorder = null;
        try {
            grabber.start();

            recorder = recorderFactory.create(outputPath, grabber);

            for (var step : steps) {
                step.accept(grabber, recorder);
            }

            recorder.start();

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                if (frame.samples != null) {
                    recorder.record(frame);
                }
            }
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
