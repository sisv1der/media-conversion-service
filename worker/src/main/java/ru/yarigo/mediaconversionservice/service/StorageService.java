package ru.yarigo.mediaconversionservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.storage.StorageProvider;
import ru.yarigo.mediaconversionservice.storage.exception.S3StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageService {

    private final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final StorageProvider storageProvider;

    public StorageService(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public void upload(String key, Path path) {
        try {
            long length = path.toFile().length();
            try (var stream = Files.newInputStream(path)) {
                var bytes = stream.readAllBytes();
                storageProvider.upload(key, new ByteArrayInputStream(bytes), length, "application/octet-stream");
            }
        } catch (IOException ex) {
            logger.debug("Error while uploading file: s3-key={}, filename={}", key, path, ex);
            throw new S3StorageException("Error while uploading file", ex);
        }
    }

    public InputStream download(String key) {
        try {
            return storageProvider.download(key);
        } catch (Exception ex) {
            logger.debug("Error while downloading file: s3-key={}", key, ex);
            throw new S3StorageException("Error while downloading file", ex);
        }
    }

    public void delete(String key) {
        try {
            storageProvider.delete(key);
        } catch (Exception ex) {
            logger.debug("Error while deleting file: s3-key={}", key, ex);
            throw new S3StorageException("Error while deleting file", ex);
        }
    }
}
