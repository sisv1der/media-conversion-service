package ru.yarigo.mediaconversionservice.config.worker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "worker.api")
public class ApiProperties {

    private String uri;

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
}
