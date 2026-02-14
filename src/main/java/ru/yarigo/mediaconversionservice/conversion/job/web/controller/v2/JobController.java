package ru.yarigo.mediaconversionservice.conversion.job.web.controller.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.job.service.JobService;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v2/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500"})
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("outputFormat") MediaFormat outputFormat
    ) throws IOException {
        return ok(jobService.create(file, outputFormat));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getById(@PathVariable UUID jobId) {
        return ok(jobService.getById(jobId));
    }

    @GetMapping("/{jobId}/file")
    public ResponseEntity<?> getFile(@PathVariable UUID jobId) throws IOException {
        var response = jobService.getFileByJobId(jobId);
        InputStreamResource inputStreamResource = new InputStreamResource(response.inputStream());

        return ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"converted." + response.outputFormat().name().toLowerCase() + "\""
                )
                .body(inputStreamResource);
    }
}
