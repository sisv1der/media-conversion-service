package ru.yarigo.mediaconversionservice.workspace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.yarigo.mediaconversionservice.job.exception.FileProcessingFailedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@Slf4j
@Component
public class MultipartFileWorkspace implements FileWorkspace<MultipartFile> {

    @Override
    public <T> T execute(MultipartFile file, String prefix, String extension, Function<Path, T> pipeline) {
        Path tempFile =  null;
        try {
            tempFile = Files.createTempFile(prefix, extension);

            file.transferTo(tempFile);

            return pipeline.apply(tempFile);
        } catch (IOException e) {
            log.warn("Workspace failed: {}", e.getMessage(), e);
            throw new FileProcessingFailedException("Workspace failed", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException _) {}
            }
        }
    }
}
