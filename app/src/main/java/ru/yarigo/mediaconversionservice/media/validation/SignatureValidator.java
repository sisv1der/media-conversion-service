package ru.yarigo.mediaconversionservice.media.validation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface SignatureValidator extends Validator {

    byte[] signature();

    @Override
    default boolean isValid(Path file) {
        byte[] signature = signature();
        byte[] header = new byte[signature.length];

        try (InputStream is = Files.newInputStream(file)) {
            if (is.read(header) != header.length) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            System.out.println(header[i] + "    " + signature[i]);
            if (header[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
