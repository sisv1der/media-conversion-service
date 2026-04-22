package ru.yarigo.mediaconversionservice.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicProperties {

    private Topics topics;

    @Setter
    @Getter
    public static class Topics {
        private String input;
        private String output;
    }
}
