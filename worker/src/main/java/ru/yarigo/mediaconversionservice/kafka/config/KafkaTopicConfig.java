package ru.yarigo.mediaconversionservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic inputTopic(KafkaTopicProperties kafkaTopicProperties) {
        return new NewTopic(kafkaTopicProperties.getTopics().getInput(), 1, (short) 1);
    }

    @Bean
    public NewTopic outputTopic(KafkaTopicProperties kafkaTopicProperties) {
        return new NewTopic(kafkaTopicProperties.getTopics().getOutput(), 1, (short) 1);
    }
}
