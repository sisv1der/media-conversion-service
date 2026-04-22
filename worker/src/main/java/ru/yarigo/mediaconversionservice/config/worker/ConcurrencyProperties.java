package ru.yarigo.mediaconversionservice.config.worker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "worker.concurrency")
public class ConcurrencyProperties {

    private int threads;

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
