package ru.yarigo.mediaconversionservice.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.storage.S3StorageProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class StorageService {

    S3StorageProvider storageProvider;

    public void upload(String key, Path path) throws IOException {
        try (var stream = Files.newInputStream(path)) {
            long length = path.toFile().length();

            storageProvider.upload(key, stream, length, "application/octet-stream");
        }
    }

    public InputStream download(String key) throws IOException {
        return storageProvider.download(key);
    }
}
