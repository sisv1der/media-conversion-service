package ru.yarigo.mediaconverionservice.validation.impl;

import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.validation.SignatureValidator;

public class PngValidator implements SignatureValidator {

    private static final byte[] PNG_SIGNATURE = new byte[] {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };

    @Override
    public MediaFormat mediaFormat() {
        return MediaFormat.PNG;
    }

    @Override
    public byte[] signature() {
        return PNG_SIGNATURE;
    }
}
