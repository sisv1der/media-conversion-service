package ru.yarigo.mediaconversionservice.storage.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yarigo.mediaconversionservice.storage.ClientProvider;
import ru.yarigo.mediaconversionservice.storage.S3ClientProvider;
import ru.yarigo.mediaconversionservice.storage.S3StorageProvider;
import ru.yarigo.mediaconversionservice.storage.StorageProvider;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageAutoConfig {

    @Bean
    @ConditionalOnMissingBean(ClientProvider.class)
    public S3ClientProvider s3ClientProvider(StorageProperties props) {
        return new S3ClientProvider(props);
    }

    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    public S3Client s3Client(S3ClientProvider provider) {
        return provider.createClient();
    }

    @Bean
    @ConditionalOnMissingBean(StorageProvider.class)
    public StorageProvider storageProvider(S3Client client, StorageProperties props) {
        return new S3StorageProvider(client, props.getBucket());
    }
}
