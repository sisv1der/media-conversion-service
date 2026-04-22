package ru.yarigo.mediaconverionservice.conversion.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yarigo.mediaconverionservice.conversion.ConversionKey;
import ru.yarigo.mediaconverionservice.conversion.Convertible;
import ru.yarigo.mediaconverionservice.conversion.MediaFormat;
import ru.yarigo.mediaconverionservice.conversion.exception.ConversionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class PngToJpgConverter implements Convertible {

    private final Logger logger = LoggerFactory.getLogger(PngToJpgConverter.class);

    public ConversionKey key() {
        return new ConversionKey(MediaFormat.PNG, MediaFormat.JPG);
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
            throw new ConversionException("Error reading image " + inputPath);
        }

        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        boolean ok;
        try {
            ok = ImageIO.write(rgb, "jpg", outputPath.toFile());
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
