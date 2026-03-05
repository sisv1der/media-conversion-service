package ru.yarigo.mediaconversionservice.conversion.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yarigo.mediaconversionservice.conversion.ConversionKey;
import ru.yarigo.mediaconversionservice.conversion.Convertible;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.exception.ConversionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
class JpgToPngConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.JPG, MediaFormat.PNG);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) {
        BufferedImage image;
        try {
            image = ImageIO.read(inputPath.toFile());
        } catch (IOException e) {
            log.warn("Error reading image from file {}", inputPath, e);
            throw new ConversionException("Error reading image " + inputPath, e);
        }
        if (image == null) {
            log.debug("Error reading image from file {}", inputPath);
            throw new ConversionException("Error reading image " + inputPath);
        }

        boolean ok;
        try {
            ok = ImageIO.write(image, "png", outputPath.toFile());
        } catch (IOException e) {
            log.warn("Error writing image to file {}", outputPath, e);
            throw new ConversionException("Error writing image " + outputPath, e);
        }
        if (!ok) {
            log.warn("Error writing image to file {}", outputPath);
            throw new ConversionException("Error writing image " + outputPath);
        }
    }
}
