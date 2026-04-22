package ru.yarigo.mediaconversionservice.job.service;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaFormatMapper {

    MediaFormat map(ru.yarigo.mediaconversionservice.job.model.MediaFormat mediaFormat);

    @InheritInverseConfiguration
    ru.yarigo.mediaconversionservice.job.model.MediaFormat map(MediaFormat mediaFormat);
}
