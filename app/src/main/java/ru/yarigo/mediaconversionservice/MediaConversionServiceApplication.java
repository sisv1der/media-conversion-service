package ru.yarigo.mediaconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.yarigo.mediaconversionservice.config.AppConfig;
import ru.yarigo.mediaconversionservice.config.AppProperties;

import ru.yarigo.mediaconversionservice.storage.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class, AppProperties.class, AppConfig.class})
@EnableAsync
public class MediaConversionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaConversionServiceApplication.class, args);
    }
}
