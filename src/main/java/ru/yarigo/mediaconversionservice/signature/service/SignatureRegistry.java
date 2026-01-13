package ru.yarigo.mediaconversionservice.signature.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.signature.SignatureValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SignatureRegistry {

    private final Map<MediaFormat, SignatureValidator> validatorMap;

    public SignatureRegistry(List<SignatureValidator> validatorList) {
        this.validatorMap = validatorList.stream()
                .collect(Collectors.toMap(
                        SignatureValidator::mediaFormat,
                        Function.identity()
                ));
    }

    public Optional<SignatureValidator> get(MediaFormat mediaFormat) {
        return Optional.ofNullable(validatorMap.get(mediaFormat));
    }
}
