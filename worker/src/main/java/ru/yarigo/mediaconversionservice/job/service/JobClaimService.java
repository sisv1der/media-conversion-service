package ru.yarigo.mediaconversionservice.job.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.config.worker.ApiProperties;
import ru.yarigo.mediaconversionservice.job.exception.JobClaimingException;
import ru.yarigo.mediaconversionservice.job.model.JobEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class JobClaimService {

    private final Logger logger = LoggerFactory.getLogger(JobClaimService.class);

    private final HttpClient httpClient;
    private final ApiProperties apiProperties;

    public JobClaimService(HttpClient httpClient, ApiProperties apiProperties) {
        this.httpClient = httpClient;
        this.apiProperties = apiProperties;
    }

    @Retryable(
            includes = JobClaimingException.class,
            maxRetries = 4,
            delay = 100,
            multiplier = 2,
            jitter = 20,
            maxDelay = 1000
    )
    public CompletableFuture<Boolean> claimAsync(JobEvent event) {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(new URI(apiProperties.getUri() + "/v2/jobs/" + event.jobId() + "/claim"))
                    .GET()
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200)
                    .exceptionally(ex -> {
                        logger.debug("Job claiming failed. JobId: {}, error: {}", event.jobId(), ex.getMessage(), ex);
                        return false;
                    });
        } catch (URISyntaxException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
