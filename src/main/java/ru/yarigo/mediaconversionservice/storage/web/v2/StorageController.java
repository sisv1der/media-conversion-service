package ru.yarigo.mediaconversionservice.storage.web.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/storage")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500"})
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("outputFormat") MediaFormat outputFormat
    ) {
        var response = storageService.upload(file, outputFormat);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Valid
    public ResponseEntity<?> getFile(
            @PathVariable @org.hibernate.validator.constraints.UUID
            UUID id
    ) {
        throw new UnsupportedOperationException("Not supported yet.");
        //TODO: реализовать возврат потока байт
    }
}
