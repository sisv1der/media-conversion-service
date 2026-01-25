package ru.yarigo.mediaconversionservice.validation.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
class JpgValidator implements Validator {

    private static final byte[] JPEG_SIGNATURE = new byte[] {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
    };

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.JPG;
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

        for (int i = 0; i < JPEG_SIGNATURE.length; i++) {
            if (header[i] != JPEG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
}
