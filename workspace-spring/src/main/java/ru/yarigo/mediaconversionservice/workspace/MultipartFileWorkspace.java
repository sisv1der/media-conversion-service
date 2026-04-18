package ru.yarigo.mediaconversionservice.workspace;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class MultipartFileWorkspace implements FileWorkspace<MultipartFile> {

    @Override
    public <T> T execute(MultipartFile file, String prefix, String extension, Function<Path, T> pipeline) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(prefix, extension);

            file.transferTo(tempFile);

            return pipeline.apply(tempFile);
        } catch (IOException e) {
            throw new WorkspaceException("Workspace failed", e);
        } finally {
            if (tempFile != null) {
                try {
                    Files.delete(tempFile);
                } catch (IOException _) {}
            }
        }
    }
}