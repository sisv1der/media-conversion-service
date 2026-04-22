package ru.yarigo.mediaconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ru.yarigo.mediaconversionservice.config.AppConfig;
import ru.yarigo.mediaconversionservice.config.AppProperties;
import ru.yarigo.mediaconversionservice.storage.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class, AppConfig.class, AppProperties.class})
public class WorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
