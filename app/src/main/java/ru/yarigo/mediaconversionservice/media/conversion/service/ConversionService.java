package ru.yarigo.mediaconversionservice.media.conversion.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.media.conversion.ConverterRegistry;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.media.conversion.exception.UnsupportedMediaFormatException;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final ConverterRegistry converterRegistry;

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
