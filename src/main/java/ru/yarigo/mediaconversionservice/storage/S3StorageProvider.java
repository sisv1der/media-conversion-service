package ru.yarigo.mediaconversionservice.storage;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

public class S3StorageProvider implements StorageProvider {
    private final S3Client s3;
    private final String bucket;

    public S3StorageProvider(S3Client s3, String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    @Override
    public InputStream download(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3.getObject(req);
    }

    @Override
    public void upload(String key, InputStream data, long contentLength, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength(contentLength)
                .contentType(contentType)
                .build();

        s3.putObject(req, RequestBody.fromInputStream(data, contentLength));
    }

    @Override
    public String getBucket() {
        return bucket;
    }
}
