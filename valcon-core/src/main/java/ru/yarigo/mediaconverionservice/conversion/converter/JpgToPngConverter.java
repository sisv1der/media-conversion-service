package ru.yarigo.mediaconverionservice.conversion.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yarigo.mediaconverionservice.conversion.ConversionKey;
import ru.yarigo.mediaconverionservice.conversion.Convertible;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.exception.ConversionException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class JpgToPngConverter implements Convertible {

    private final Logger logger = LoggerFactory.getLogger(JpgToPngConverter.class);

    public ConversionKey key() {
        return new ConversionKey(MediaFormat.JPG, MediaFormat.PNG);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) {
        BufferedImage image;
        try {
            image = ImageIO.read(inputPath.toFile());
        } catch (IOException e) {
            logger.debug("Error reading image from file {}", inputPath, e);
            throw new ConversionException("Error reading image " + inputPath, e);
        }
        if (image == null) {
            logger.debug("Error reading image from file {}", inputPath);
            throw new ConversionException("Error reading image " + inputPath);
        }

        boolean ok;
        try {
            ok = ImageIO.write(image, "png", outputPath.toFile());
        } catch (IOException e) {
            logger.debug("Error writing image to file {}", outputPath, e);
            throw new ConversionException("Error writing image " + outputPath, e);
        }
        if (!ok) {
            logger.debug("Error writing image to file {}", outputPath);
            throw new ConversionException("Error writing image " + outputPath);
        }
    }
}
