package ru.yarigo.mediaconversionservice.conversion.job.web.dto;

import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import java.io.InputStream;

public record FileResource(InputStream inputStream, MediaFormat outputFormat) {}
