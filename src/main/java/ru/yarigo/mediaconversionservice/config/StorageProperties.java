package ru.yarigo.mediaconversionservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "aws.s3")
public class StorageProperties {

    private String endpoint;
    private String region = "us-east-1";
    private String bucket;
    private String accessKey;
    private String secretKey;
    private boolean pathStyleAccess = true;
}