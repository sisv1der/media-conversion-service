package ru.yarigo.mediaconversionservice.job.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.yarigo.mediaconversionservice.job.sse.SseService;

@Service
@RequiredArgsConstructor
public class JobEventListener {

    private final SseService sseService;

    @TransactionalEventListener
    @Async
    public void onJobStatusUpdatedEvent(JobStatusUpdatedEvent event) {
        sseService.send(event.jobId(), event);
    }
}
