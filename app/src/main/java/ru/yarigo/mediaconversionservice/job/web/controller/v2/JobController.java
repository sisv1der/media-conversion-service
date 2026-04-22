package ru.yarigo.mediaconversionservice.job.web.controller.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.job.service.JobService;
import ru.yarigo.mediaconversionservice.job.sse.SseService;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v2/jobs")
@RequiredArgsConstructor
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://localhost:5500",
                "http://localhost:5173",
                "https://svivy.ru"
        },
        exposedHeaders = {
                "Content-Disposition"
        }
)
public class JobController {

    private final JobService jobService;
    private final SseService sseService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("outputFormat") MediaFormat outputFormat
    ) {
        return ok(jobService.create(file, outputFormat));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getById(@PathVariable UUID jobId) {
        return ok(jobService.getById(jobId));
    }

    @GetMapping("/{jobId}/stream")
    public SseEmitter getStream(@PathVariable UUID jobId) {
        return sseService.subscribe(jobId, 120_000L); // 120 seconds
    }

    @GetMapping("/{jobId}/file")
    public ResponseEntity<?> getFile(@PathVariable UUID jobId) {
        var response = jobService.getFileByJobId(jobId);
        var inputStreamResource = response.inputStream();

        return ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"converted." + response.outputFormat().name().toLowerCase() + "\""
                )
                .body(inputStreamResource);
    }

    @GetMapping
    public ResponseEntity<?> getJobs(@RequestParam List<UUID> ids) {
        return ok(jobService.getByIds(ids));
    }

    @PatchMapping("/{jobId}/claim")
    public ResponseEntity<?> claimJob(@PathVariable UUID jobId) {
        jobService.claimJob(jobId);
        return ok().build();
    }
}
