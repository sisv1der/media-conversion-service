package ru.yarigo.mediaconversionservice.conversion.job.service;

import java.util.UUID;

public class KeyGenerator {
    public static String inputKey(UUID jobId, String originalFilename) {
        return jobId + "/input/" + sanitize(originalFilename);
    }

    public static String outputKey(UUID jobId, String outputFormat) {
        return jobId + "/output/converted." + outputFormat.toLowerCase();
    }

    private static String sanitize(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
