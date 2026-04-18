package ru.yarigo.mediaconversionservice.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

public class InputStreamWorkspace implements FileWorkspace<InputStream> {

    @Override
    public <T> T execute(InputStream file, String prefix, String extension, Function<Path, T> pipeline) {
        Path tempFile =  null;
        try {
            tempFile = Files.createTempFile(prefix, extension);

            Files.copy(file, tempFile, StandardCopyOption.REPLACE_EXISTING);

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