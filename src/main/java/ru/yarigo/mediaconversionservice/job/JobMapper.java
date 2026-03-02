package ru.yarigo.mediaconversionservice.job;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.yarigo.mediaconversionservice.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.job.web.dto.ReadJobStatusResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface JobMapper {

    @Mappings({
            @Mapping(target = "jobId", source = "id"),
            @Mapping(target = "jobStatus", source = "status")
    })
    ReadJobStatusResponse map(JobEntity job);
}
