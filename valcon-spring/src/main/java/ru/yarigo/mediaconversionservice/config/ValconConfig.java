package ru.yarigo.mediaconversionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yarigo.mediaconverionservice.conversion.Convertible;
import ru.yarigo.mediaconverionservice.conversion.converter.*;
import ru.yarigo.mediaconverionservice.validation.Validator;
import ru.yarigo.mediaconverionservice.validation.impl.*;
import ru.yarigo.mediaconversionservice.conversion.ConverterRegistry;
import ru.yarigo.mediaconversionservice.validation.ValidatorRegistry;

import java.util.List;

@Configuration
public class ValconConfig {

    @Bean
    public JpgToPngConverter jpgToPngConverter() {
        return new JpgToPngConverter();
    }

    @Bean
    public Mp3ToWavConverter mp3ToWavConverter() {
        return new Mp3ToWavConverter();
    }

    @Bean
    public Mp4ToWebmConverter mp4ToWebmConverter() {
        return new Mp4ToWebmConverter();
    }

    @Bean
    public PngToJpgConverter pngToJpgConverter() {
        return new PngToJpgConverter();
    }

    @Bean
    public WavToMp3Converter wavToMp3Converter() {
        return new WavToMp3Converter();
    }

    @Bean
    public WebmToMp4Converter webmToMp4Converter() {
        return new WebmToMp4Converter();
    }

    @Bean
    public ConverterRegistry converterRegistry(List<Convertible> converters) {
        return new ConverterRegistry(converters);
    }

    @Bean
    public JpgValidator jpgValidator() {
        return new JpgValidator();
    }

    @Bean
    public PngValidator pngValidator() {
        return new PngValidator();
    }

    @Bean
    public WavValidator wavValidator() {
        return new WavValidator();
    }

    @Bean
    public Mp3Validator mp3Validator() {
        return new Mp3Validator();
    }

    @Bean
    public Mp4Validator mp4Validator() {
        return new Mp4Validator();
    }

    @Bean
    public WebmValidator webmValidator() {
        return new WebmValidator();
    }

    @Bean
    public ValidatorRegistry validatorRegistry(List<Validator> validators) {
        return new ValidatorRegistry(validators);
    }
}
