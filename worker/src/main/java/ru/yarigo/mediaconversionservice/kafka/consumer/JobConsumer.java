package ru.yarigo.mediaconversionservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.kafka.JobEvent;
import ru.yarigo.mediaconversionservice.kafka.producer.Producer;
import ru.yarigo.mediaconversionservice.job.service.JobClaimService;
import ru.yarigo.mediaconversionservice.job.service.JobService;

import static ru.yarigo.mediaconversionservice.job.model.JobStatus.FAILED;

@Service
public class JobConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobConsumer.class);

    private final JobService jobService;
    private final JobClaimService jobClaimService;
    private final Producer<JobEvent> producer;

    public JobConsumer(JobService jobService, JobClaimService jobClaimService, Producer<JobEvent> producer) {
        this.jobService = jobService;
        this.jobClaimService = jobClaimService;
        this.producer = producer;
    }

    @KafkaListener(
            topics = "${worker.kafka.topics.input}",
            groupId = "worker-group"
    )
    public void consume(JobEvent event, Acknowledgment ack) {
        jobClaimService.claimAsync(event)
                        .thenAccept(claimed -> {
                            if (!claimed) {
                                logger.debug("Job claiming failed. JobId: {}", event.jobId());
                                ack.acknowledge();
                                return;
                            }

                            jobService.process(event)
                                    .thenAccept(producer::produce)
                                    .thenRun(ack::acknowledge)
                                    .exceptionally(
                                            ex -> {
                                                producer.produce(
                                                        event
                                                                .withErrorMessage(ex.getMessage())
                                                                .withStatus(FAILED)
                                                );
                                                ack.acknowledge();
                                                return null;
                                            }
                                    );
                        });
    }
}
