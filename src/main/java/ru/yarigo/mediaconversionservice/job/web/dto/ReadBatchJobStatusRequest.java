package ru.yarigo.mediaconversionservice.job.web.dto;

import java.util.List;

public record ReadBatchJobStatusRequest(List<ReadJobStatusRequest> jobs) {}
