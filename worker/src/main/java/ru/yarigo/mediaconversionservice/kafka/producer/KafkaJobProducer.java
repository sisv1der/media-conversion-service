package ru.yarigo.mediaconversionservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.kafka.config.KafkaTopicProperties;
import ru.yarigo.mediaconversionservice.kafka.JobEvent;

@Service
public class KafkaJobProducer implements Producer<JobEvent> {

    private final KafkaTemplate<String, JobEvent> kafkaTemplate;
    private final KafkaTopicProperties props;

    public KafkaJobProducer(KafkaTemplate<String, JobEvent> kafkaTemplate, KafkaTopicProperties kafkaTopicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.props = kafkaTopicProperties;
    }

    @Override
    public void produce(JobEvent event) {
        kafkaTemplate.send(
                props.getTopics().getOutput(),
                event.jobId().toString(),
                event
        );
    }
}
