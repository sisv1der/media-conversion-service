package ru.yarigo.mediaconversionservice.media.conversion;

import java.nio.file.Path;

public interface Convertible {

    ConversionKey key();

    void convert(Path inputPath, Path outputPath);
}
