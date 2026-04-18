package ru.yarigo.mediaconversionservice.workspace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.job.exception.FileProcessingFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

@Slf4j
@Service
public class InputStreamWorkspace implements FileWorkspace<InputStream> {

    @Override
    public <T> T execute(InputStream file, String prefix, String extension, Function<Path, T> pipeline) {
        Path tempFile =  null;
        try {
            tempFile = Files.createTempFile(prefix, extension);

            Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);

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
