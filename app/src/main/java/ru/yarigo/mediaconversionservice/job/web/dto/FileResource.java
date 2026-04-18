package ru.yarigo.mediaconversionservice.job.web.dto;

import org.springframework.core.io.InputStreamResource;
import ru.yarigo.mediaconversionservice.media.conversion.MediaFormat;

public record FileResource(InputStreamResource inputStream, MediaFormat outputFormat) {}
