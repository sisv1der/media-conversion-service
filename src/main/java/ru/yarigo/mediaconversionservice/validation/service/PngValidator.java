package ru.yarigo.mediaconversionservice.validation.service;

import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
class PngValidator implements Validator {

    private static final byte[] PNG_SIGNATURE = new byte[] {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.PNG;
    }

    @Override
    public boolean isValid(Path file) {
        byte[] header = new byte[8];

        try (InputStream is = Files.newInputStream(file)) {
            if (is.read(header) != 8) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (header[i] != PNG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
}
