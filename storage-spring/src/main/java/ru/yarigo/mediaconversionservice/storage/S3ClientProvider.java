package ru.yarigo.mediaconversionservice.storage;

import ru.yarigo.mediaconversionservice.storage.config.StorageProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

public class S3ClientProvider implements ClientProvider<S3Client> {

    private final StorageProperties props;

    public S3ClientProvider(StorageProperties props) {
        this.props = props;
    }

    @Override
    public S3Client createClient() {
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
}
