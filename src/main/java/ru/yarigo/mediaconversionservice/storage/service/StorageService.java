package ru.yarigo.mediaconversionservice.storage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.storage.S3StorageProvider;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    S3StorageProvider storageProvider;

    public void upload(String key, Path path) {
        try (var stream = Files.newInputStream(path)) {
            long length = path.toFile().length();

            storageProvider.upload(key, stream, length, "application/octet-stream");
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
