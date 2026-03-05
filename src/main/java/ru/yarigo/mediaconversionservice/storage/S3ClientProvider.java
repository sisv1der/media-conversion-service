package ru.yarigo.mediaconversionservice.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.config.StorageProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class S3ClientProvider {

    private final StorageProperties props;

    public S3Client s3Client() {
        S3Configuration serviceConfig = S3Configuration.builder()
                .pathStyleAccessEnabled(props.isPathStyleAccess())
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())
                ))
                .serviceConfiguration(serviceConfig)
                .build();
    }

    public String getBucket() {
        return props.getBucket();
    }
}
