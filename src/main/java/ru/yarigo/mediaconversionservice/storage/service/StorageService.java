package ru.yarigo.mediaconversionservice.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.storage.web.v2.dto.FileUploadResponse;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    public FileUploadResponse upload(MultipartFile file, MediaFormat outputFormat) {
        throw new UnsupportedOperationException("Not supported yet.");
        //TODO: реализовать загрузку
    }

    public InputStream download(UUID id) {
        throw new UnsupportedOperationException("Not supported yet.");
        //TODO: реализовать скачивание
    }
}
