package ru.yarigo.mediaconversionservice.media.conversion.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.exception.UnsupportedMediaFormatException;
import ru.yarigo.mediaconversionservice.conversion.ConverterRegistry;

import java.nio.file.Path;

@Service
public class ConversionService {

    private final ConverterRegistry converterRegistry;

    public ConversionService(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public void convert(
            Path inputPath,
            Path outputPath,
            MediaFormat inputFormat,
            MediaFormat outputFormat
    ) {
        converterRegistry.get(inputFormat, outputFormat)
                .orElseThrow(() -> new UnsupportedMediaFormatException("Input format not supported"))
                .convert(inputPath, outputPath);
    }
}
