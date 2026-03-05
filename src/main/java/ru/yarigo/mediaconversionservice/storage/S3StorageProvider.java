package ru.yarigo.mediaconversionservice.storage;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
public class S3StorageProvider implements StorageProvider {

    private final S3Client s3;
    private final String bucket;

    public S3StorageProvider(S3ClientProvider s3ClientProvider) {
        this.s3 = s3ClientProvider.s3Client();
        this.bucket = s3ClientProvider.getBucket();
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

    public void delete(String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3.deleteObject(req);
    }
}