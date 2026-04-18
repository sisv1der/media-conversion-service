package ru.yarigo.mediaconversionservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "storage")
public class AppProperties {

    private StorageType type = StorageType.LOCAL;

    public enum StorageType { LOCAL, S3 }
}
