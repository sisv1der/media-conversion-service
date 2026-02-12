package ru.yarigo.mediaconversionservice.validation;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ValidatorRegistry {

    private final Map<MediaFormat, Validator> validatorMap;

    public ValidatorRegistry(List<Validator> validatorList) {
        this.validatorMap = validatorList.stream()
                .collect(Collectors.toMap(
                        Validator::mediaFormat,
                        Function.identity()
                ));
    }

    public Optional<Validator> get(MediaFormat mediaFormat) {
        return Optional.ofNullable(validatorMap.get(mediaFormat));
    }
}
