package ru.yarigo.mediaconversionservice.conversion;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import ru.yarigo.mediaconversionservice.conversion.exception.UnsupportedMediaFormatException;

import java.util.Arrays;

@Getter
public enum MediaFormat {

    MP4("mp4"),
    WEBM("webm"),
    MP3("mp3"),
    WAV("wav"),
    PNG("png"),
    JPG("jpg");

    private final String extension;

    MediaFormat(String extension) {
        this.extension = extension;
    }

    public static MediaFormat getMediaFormat(String filename) throws  UnsupportedMediaFormatException {
        var extension = FilenameUtils.getExtension(filename).toLowerCase().replaceAll("^\\.", "");

        return Arrays.stream(MediaFormat.values())
                .filter(format -> format.getExtension().equals(extension))
                .findFirst()
                .orElseThrow(() -> new UnsupportedMediaFormatException("Extension " + extension + " is not supported"));
    }
}
