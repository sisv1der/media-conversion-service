package ru.yarigo.mediaconversionservice.conversion.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.ConversionKey;
import ru.yarigo.mediaconversionservice.conversion.Convertible;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;
import ru.yarigo.mediaconversionservice.conversion.exception.ConversionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class PngToJpgConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.PNG, MediaFormat.JPG);
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
            log.warn("Error writing image to file {}", outputPath, e);
            throw new ConversionException("Error writing image " + outputPath, e);
        }
        if (!ok) {
            log.warn("Error writing image to file {}", outputPath);
            throw new ConversionException("Error writing image " + outputPath);
        }
    }
}
