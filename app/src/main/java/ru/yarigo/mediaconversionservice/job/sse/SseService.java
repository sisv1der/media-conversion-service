package ru.yarigo.mediaconversionservice.job.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> storage = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID jobId, Long timeout) {
        return put(jobId, timeout);
    }

    public void send(UUID jobId, Object event) {
        List<SseEmitter> emitters = storage.get(jobId);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                emitter.completeWithError(e);
                remove(jobId);
            }
        }
    }

    private void remove(UUID jobId) {
        storage.remove(jobId);
    }

    private void remove(UUID jobId, SseEmitter emitter) {
        var emitters = storage.get(jobId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    private SseEmitter put(UUID jobId, Long timeout) {
        var emitter = new SseEmitter(timeout);
        emitter.onCompletion(() -> remove(jobId, emitter));
        emitter.onTimeout(() -> remove(jobId, emitter));

        storage.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        return emitter;
    }
}
