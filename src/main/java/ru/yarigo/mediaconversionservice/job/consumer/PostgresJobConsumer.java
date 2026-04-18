package ru.yarigo.mediaconversionservice.job.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.job.exception.JobProcessingException;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.job.processor.JobProcessor;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class PostgresJobConsumer implements JobConsumer {

    private final JobRepository jobRepository;
    private final Executor executor;
    private final JobProcessor<JobEntity> jobProcessor;

    public PostgresJobConsumer(
            JobRepository jobRepository,
            @Qualifier("conversionExecutor") Executor executor,
            JobProcessor<JobEntity> jobProcessor
    ) {
        this.jobRepository = jobRepository;
        this.executor = executor;
        this.jobProcessor = jobProcessor;
    }

    @Scheduled(fixedRate = 5000)
    @Override
    public void process() {
        int JOB_LIMIT = 10;
        var jobs = pickJobsToProcess(JOB_LIMIT);

        for (JobEntity job : jobs) {
           executor.execute(() -> {
               try {
                   jobProcessor.process(job);
               } catch (JobProcessingException e) {
                   throw new JobProcessingException("Error processing job " + job.getId(), e);
               }
           });
        }
    }

    private List<JobEntity> pickJobsToProcess(int limit) {
        return jobRepository.findByStatus(limit);
    }
}
