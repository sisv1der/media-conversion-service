package ru.yarigo.mediaconversionservice.converter.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.ConversionKey;
import ru.yarigo.mediaconversionservice.converter.Converter;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConverterRegistry {

    private final Map<ConversionKey, Converter> converterMap;

    public ConverterRegistry(List<Converter> converterList) {
        this.converterMap = converterList.stream()
                .collect(Collectors.toMap(
                        Converter::key,
                        Function.identity()
                ));
    }

    public Optional<Converter> get(MediaFormat in, MediaFormat out) {
        return Optional.ofNullable(converterMap.get(new ConversionKey(in, out)));
    }
}
