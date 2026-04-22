package ru.yarigo.mediaconversionservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public class AppProperties {

    private StorageType type = StorageType.LOCAL;

    public enum StorageType { LOCAL, S3 }

    public StorageType getType() {
        return type;
    }

    public void setType(StorageType type) {
        this.type = type;
    }
}
