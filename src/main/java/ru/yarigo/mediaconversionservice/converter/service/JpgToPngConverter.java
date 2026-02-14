package ru.yarigo.mediaconversionservice.converter.service;

import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.converter.ConversionKey;
import ru.yarigo.mediaconversionservice.converter.Convertible;
import ru.yarigo.mediaconversionservice.converter.MediaFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Service
class JpgToPngConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.JPG, MediaFormat.PNG);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) throws IOException {
        BufferedImage image = ImageIO.read(inputPath.toFile());
        if (image == null) {
            throw new IllegalStateException("Не удалось прочитать изображение");
        }

        boolean ok = ImageIO.write(image, "png", outputPath.toFile());
        if (!ok) {
            throw new IllegalStateException("ImageIO не смог записать PNG");
        }
    }
}
