package ru.yarigo.mediaconversionservice.conversion.legacy.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.ConverterRegistry;
import ru.yarigo.mediaconversionservice.validation.ValidatorRegistry;

import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final ConverterRegistry converterRegistry;
    private final ValidatorRegistry validatorRegistry;

    public byte[] convert(
            MultipartFile file,
            MediaFormat inputFormat,
            MediaFormat outputFormat
    ) throws IOException {
        var inputPath = Files.createTempFile(
                "input-",
                "." + inputFormat.getExtension()
        );
        file.transferTo(inputPath);

        var isNotValid = validatorRegistry.get(inputFormat)
                .orElseThrow(UnsupportedOperationException::new)
                .isNotValid(inputPath);
        if (isNotValid) {
            throw new BadRequestException("Неверная сигнатура файла");
        }

        var outputPath = Files.createTempFile(
                "output-",
                "." + outputFormat.getExtension()
        );
        try {
            converterRegistry.get(inputFormat, outputFormat)
                    .orElseThrow(() -> new UnsupportedOperationException(
                            "Такой формат не поддерживается: " + inputFormat + " на " + outputFormat
                    )).convert(inputPath, outputPath);

            return Files.readAllBytes(outputPath);
        } finally {
            Files.deleteIfExists(inputPath);
            Files.deleteIfExists(outputPath);
        }
    }
}
