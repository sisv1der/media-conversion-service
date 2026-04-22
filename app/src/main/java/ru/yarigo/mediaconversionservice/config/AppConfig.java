package ru.yarigo.mediaconversionservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yarigo.mediaconversionservice.kafka.config.KafkaTopicProperties;
import ru.yarigo.mediaconversionservice.workspace.InputStreamWorkspace;
import ru.yarigo.mediaconversionservice.workspace.MultipartFileWorkspace;

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class AppConfig {

    @Bean
    public InputStreamWorkspace inputStreamWorkspace() {
        return new InputStreamWorkspace();
    }

    @Bean
    public MultipartFileWorkspace multipartFileWorkspace() {
        return new MultipartFileWorkspace();
    }
}
