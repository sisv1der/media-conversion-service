package ru.yarigo.mediaconversionservice.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface Convertible {

    ConversionKey key();

    void convert(
            Path inputPath,
            Path outputPath
    ) throws IOException;

    default void convert(
            Path inputPath,
            Path outputPath,
            Map<String, String> params
    ) {
        throw new UnsupportedOperationException();
    }
}
