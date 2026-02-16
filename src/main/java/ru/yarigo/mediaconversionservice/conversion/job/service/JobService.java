package ru.yarigo.mediaconversionservice.conversion.job.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.MediaFormatMapper;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobStatus;
import ru.yarigo.mediaconversionservice.conversion.job.web.dto.CreateJobResponse;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobEntity;
import ru.yarigo.mediaconversionservice.conversion.job.model.JobRepository;
import ru.yarigo.mediaconversionservice.conversion.job.web.dto.FileResource;
import ru.yarigo.mediaconversionservice.conversion.job.web.dto.ReadJobStatusResponse;
import ru.yarigo.mediaconversionservice.storage.service.StorageService;
import ru.yarigo.mediaconversionservice.validation.service.ValidationService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final ValidationService validationService;
    private final StorageService storageService;
    private final MediaFormatMapper mediaFormatMapper;

    public FileResource getFileByJobId(UUID jobId) {
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        if (job.getStatus() == JobStatus.DONE) {
            return new FileResource(
                    storageService.download(job.getOutputS3Key()),
                    mediaFormatMapper.map(job.getOutputFormat())
            );
        }
        throw new IllegalStateException("Job has not been done"); // 425 Too Early
    }

    public ReadJobStatusResponse getById(UUID jobId) {
        var job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        return new ReadJobStatusResponse(job.getId(), job.getStatus().name());
    }

    /*
     TODO: Обработать разные статусы задания
      - PENDING/PROCESSING → 425 Too Early
      - FAILED → 404 или 200
      - Сейчас при не-DONE кидается IllegalStateException → клиент получает 500
    */

    public CreateJobResponse create(
            MultipartFile file,
            ru.yarigo.mediaconversionservice.conversion.MediaFormat outputFormat
    ) throws IOException {
        var inputFormat = MediaFormat.getMediaFormat(file.getOriginalFilename());
        var inputPath = Files.createTempFile(
                "input-",
                "." + inputFormat.getExtension()
        );
        file.transferTo(inputPath);
        try {
            if (validationService.isNotValid(inputPath, inputFormat)) {
                throw new IllegalStateException("Invalid input");
            }
            var jobId = UUID.randomUUID();
            var inputKey = KeyGenerator.inputKey(jobId, file.getOriginalFilename());
            var job = JobEntity.builder()
                    .id(jobId)
                    .filename(file.getOriginalFilename())
                    .inputS3Key(inputKey)
                    .inputFormat(mediaFormatMapper.map(inputFormat))
                    .outputFormat(mediaFormatMapper.map(outputFormat))
                    .build();

            storageService.upload(inputKey, inputPath);
            jobRepository.save(job);
            // TODO: продумать компенсационные действия в случае падения S3 или БД (это зависимые друг от друга операции)

            return new CreateJobResponse(jobId, JobStatus.PENDING.name());
        } finally {
            Files.deleteIfExists(inputPath);
        }
    }
}
