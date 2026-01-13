package ru.yarigo.mediaconversionservice.converter;

import java.io.IOException;
import java.nio.file.Path;

public interface Converter {

    ConversionKey key();

    void convert(
            Path inputPath,
            Path outputPath
    ) throws IOException;
}
