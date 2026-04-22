package ru.yarigo.mediaconversionservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.kafka.JobEvent;
import ru.yarigo.mediaconversionservice.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.job.service.JobService;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobConsumer {

    private final JobService jobService;

    @KafkaListener(
            topics = "${worker.kafka.topics.output}",
            groupId = "app-group"
    )
    public void consume(JobEvent event, Acknowledgment ack) {
        int updated = switch (event.status()) {
            case DONE, FAILED -> jobService.updateJobStatus(event.jobId(), JobStatus.PROCESSING, event);
            default -> {
                log.error("Unknown event status: {}", event.status());
                ack.acknowledge();
                throw new IllegalArgumentException("Unknown event status: " + event.status());
            }
        };

        if (updated == 0) {
            log.warn("Event ignored: {}", event);
        }

        ack.acknowledge();
    }
}
