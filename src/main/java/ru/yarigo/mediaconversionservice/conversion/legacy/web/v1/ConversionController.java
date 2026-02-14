package ru.yarigo.mediaconversionservice.conversion.legacy.web.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.legacy.service.ConversionService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/convert")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500"})
public class ConversionController {

    private final ConversionService conversionService;

    @PostMapping("")
    public ResponseEntity<?> convert(
            @RequestParam("file") MultipartFile file,
            @RequestParam("inputFormat") MediaFormat inputFormat,
            @RequestParam("outputFormat") MediaFormat outputFormat
    ) throws IOException {
        var outputFile = conversionService.convert(file, inputFormat, outputFormat);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"converted." + outputFormat.name().toLowerCase() + "\""
                ).body(outputFile);
    }
}
