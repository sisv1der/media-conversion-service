package ru.yarigo.mediaconversionservice.conversion;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaFormatMapper {

    MediaFormat map(ru.yarigo.mediaconversionservice.conversion.job.model.MediaFormat mediaFormat);

    @InheritInverseConfiguration
    ru.yarigo.mediaconversionservice.conversion.job.model.MediaFormat map(MediaFormat mediaFormat);
}
