package ru.yarigo.mediaconversionservice.conversion.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.ConverterRegistry;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import java.io.IOException;
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
    ) throws IOException {
        converterRegistry.get(inputFormat, outputFormat)
                .orElseThrow(() -> new IllegalArgumentException("Input format not supported"))
                .convert(inputPath, outputPath);
    }
}
