package ru.yarigo.mediaconversionservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;
import ru.yarigo.mediaconversionservice.config.worker.ApiProperties;
import ru.yarigo.mediaconversionservice.workspace.InputStreamWorkspace;

import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
@EnableResilientMethods
public class AppConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public InputStreamWorkspace inputStreamWorkspace() {
        return new InputStreamWorkspace();
    }
}
