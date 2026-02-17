package ru.yarigo.mediaconversionservice.conversion.job.web.dto;

import org.springframework.core.io.InputStreamResource;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

public record FileResource(InputStreamResource inputStream, MediaFormat outputFormat) {}
