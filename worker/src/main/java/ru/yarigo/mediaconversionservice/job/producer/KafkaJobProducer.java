package ru.yarigo.mediaconversionservice.job.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.config.worker.kafka.KafkaTopicProperties;
import ru.yarigo.mediaconversionservice.job.model.JobEvent;

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
