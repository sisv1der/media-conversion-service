package ru.yarigo.mediaconverionservice.conversion.engine;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yarigo.mediaconverionservice.conversion.exception.ConversionException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FfmpegPipeline {

    private final Logger logger = LoggerFactory.getLogger(FfmpegPipeline.class);

    private final Path inputPath;
    private final Path outputPath;
    private final RecorderFactory recorderFactory;
    private final List<BiConsumer<FFmpegFrameGrabber, FFmpegFrameRecorder>> steps = new ArrayList<>();
    private final List<Consumer<FFmpegFrameGrabber>> grabberSteps = new ArrayList<>();

    public FfmpegPipeline(Path inputPath, Path outputPath, RecorderFactory recorderFactory) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.recorderFactory = recorderFactory;
    }

    public FfmpegPipeline grabberStep(Consumer<FFmpegFrameGrabber> step) {
        grabberSteps.add(step);
        return this;
    }

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
            for (var step : grabberSteps) {
                step.accept(grabber);
            }

            grabber.start();

            recorder = recorderFactory.create(outputPath, grabber);

            for (var step : steps) {
                step.accept(grabber, recorder);
            }

            recorder.start();

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                recorder.record(frame);
            }
        } catch (FFmpegFrameRecorder.Exception | FFmpegFrameGrabber.Exception e) {
            logger.debug("FFmpeg conversion failed: input={}, output={}", inputPath, outputPath, e);
            throw new ConversionException("FFmpeg pipeline error", e);
        }
        finally {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            grabber.stop();
            grabber.release();
        }
    }
}
