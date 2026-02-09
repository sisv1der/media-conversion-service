package ru.yarigo.mediaconversionservice.storage;

import java.io.InputStream;

public interface StorageProvider {

    InputStream download(String key);
    void upload(String key, InputStream data, long contentLength, String contentType);
    String getBucket();
}