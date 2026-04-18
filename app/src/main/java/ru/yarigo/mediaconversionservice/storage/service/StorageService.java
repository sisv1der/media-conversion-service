package ru.yarigo.mediaconversionservice.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.storage.StorageProvider;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageProvider storageProvider;

    public void upload(String key, Path path) {
        try {
            long length = path.toFile().length();
            try (var stream = Files.newInputStream(path)) {
                var bytes = stream.readAllBytes();
                storageProvider.upload(key, new ByteArrayInputStream(bytes), length, "application/octet-stream");
            }
        } catch (IOException ex) {
            log.warn("Error while uploading file: s3-key={}, filename={}", key, path, ex);
            throw new S3StorageException("Error while uploading file", ex);
        }
    }

    public InputStream download(String key) {
        try {
            return storageProvider.download(key);
        } catch (Exception ex) {
            log.error("Error while downloading file: s3-key={}", key, ex);
            throw new S3StorageException("Error while downloading file", ex);
        }
    }

    public void delete(String key) {
        try {
            storageProvider.delete(key);
        } catch (Exception ex) {
            log.error("Error while deleting file: s3-key={}", key, ex);
            throw new S3StorageException("Error while deleting file", ex);
        }
    }
}
