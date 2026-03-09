package ru.yarigo.mediaconversionservice.validation.impl;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.validation.SignatureValidator;

@Service
public class JpgValidator implements SignatureValidator {

    private static final byte[] JPEG_SIGNATURE = new byte[] {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
    };

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.JPG;
    }

    @Override
    public byte[] signature() {
        return JPEG_SIGNATURE;
    }
}
