package ru.yarigo.mediaconversionservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties({StorageProperties.class, AppProperties.class})
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class MinioConfig {

    @Bean
    public S3Client s3Client(StorageProperties props) {
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

    @Bean
    public ru.yarigo.mediaconversionservice.storage.StorageProvider storageProvider(S3Client s3Client, StorageProperties props) {
        return new ru.yarigo.mediaconversionservice.storage.S3StorageProvider(s3Client, props.getBucket());
    }
}