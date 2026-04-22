package ru.yarigo.mediaconversionservice.config.worker;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.yarigo.mediaconversionservice.config.worker.kafka.KafkaTopicProperties;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties({KafkaTopicProperties.class, ConcurrencyProperties.class})
public class WorkerConfig {

    @Bean("conversionExecutor")
    public ThreadPoolTaskExecutor conversionExecutor(ConcurrencyProperties concurrencyProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(concurrencyProperties.getThreads() / 2);
        executor.setMaxPoolSize(concurrencyProperties.getThreads());
        executor.setQueueCapacity(concurrencyProperties.getThreads() * 2);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("conversion-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }
}