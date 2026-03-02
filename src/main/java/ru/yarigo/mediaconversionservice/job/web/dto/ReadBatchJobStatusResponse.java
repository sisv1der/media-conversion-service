package ru.yarigo.mediaconversionservice.job.web.dto;

import java.util.List;

public record ReadBatchJobStatusResponse(List<ReadJobStatusResponse> jobs) {}
