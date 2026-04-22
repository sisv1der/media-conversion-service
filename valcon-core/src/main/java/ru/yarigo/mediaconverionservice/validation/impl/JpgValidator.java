package ru.yarigo.mediaconverionservice.validation.impl;

import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.validation.SignatureValidator;

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
