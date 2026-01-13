package ru.yarigo.mediaconversionservice.converter;

import lombok.Getter;

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

}
