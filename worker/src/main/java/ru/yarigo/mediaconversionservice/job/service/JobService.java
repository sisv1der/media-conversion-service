package ru.yarigo.mediaconversionservice.job.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.job.model.JobEvent;
import ru.yarigo.mediaconversionservice.job.processor.JobProcessor;

import java.util.concurrent.CompletableFuture;

@Service
public class JobService {

    private final JobProcessor<JobEvent> processor;
    private final ThreadPoolTaskExecutor executor;

    public JobService(JobProcessor<JobEvent> processor, @Qualifier("conversionExecutor") ThreadPoolTaskExecutor executor) {
        this.processor = processor;
        this.executor = executor;
    }

    public CompletableFuture<JobEvent> process(JobEvent event) {
        return CompletableFuture.supplyAsync(() -> processor.process(event), executor);
    }
}
