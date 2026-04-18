package ru.yarigo.mediaconversionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yarigo.mediaconversionservice.workspace.InputStreamWorkspace;
import ru.yarigo.mediaconversionservice.workspace.MultipartFileWorkspace;

@Configuration
public class AppConfig {

    @Bean
    public InputStreamWorkspace inputStreamWorkspace() {
        return new InputStreamWorkspace();
    }

    @Bean
    public MultipartFileWorkspace multipartFileWorkspace() {
        return new MultipartFileWorkspace();
    }
}
