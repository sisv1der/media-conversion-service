package ru.yarigo.mediaconversionservice.converter;

import java.io.IOException;
import java.nio.file.Path;

public interface Convertible {

    ConversionKey key();

    void convert(
            Path inputPath,
            Path outputPath
    ) throws IOException;
}
