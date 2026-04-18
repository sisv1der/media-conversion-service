package ru.yarigo.mediaconversionservice.workspace;

import java.nio.file.Path;
import java.util.function.Function;

public interface FileWorkspace<FT> {

    <T> T execute(FT file, String prefix, String extension, Function<Path, T> pipeline);
}
