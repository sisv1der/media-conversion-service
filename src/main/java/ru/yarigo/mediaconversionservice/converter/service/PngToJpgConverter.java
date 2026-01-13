package ru.yarigo.mediaconversionservice.converter.service;

import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.converter.ConversionKey;
import ru.yarigo.mediaconversionservice.converter.Converter;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PngToJpgConverter implements Converter {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.PNG, MediaFormat.JPG);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) throws IOException {
        BufferedImage image = ImageIO.read(inputPath.toFile());
        ImageIO.write(image, "jpg", outputPath.toFile());
    }
}
