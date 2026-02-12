package ru.yarigo.mediaconversionservice.conversion.converter;

import org.springframework.stereotype.Component;
import ru.yarigo.mediaconversionservice.conversion.ConversionKey;
import ru.yarigo.mediaconversionservice.conversion.Convertible;
import ru.yarigo.mediaconversionservice.conversion.MediaFormat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PngToJpgConverter implements Convertible {

    @Override
    public ConversionKey key() {
        return new ConversionKey(MediaFormat.PNG, MediaFormat.JPG);
    }

    @Override
    public void convert(Path inputPath, Path outputPath) throws IOException {
        BufferedImage image = ImageIO.read(inputPath.toFile());
        if (image == null) {
            throw new IllegalStateException("Не удалось прочитать изображение");
        }

        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        boolean ok = ImageIO.write(rgb, "jpg", outputPath.toFile());
        if (!ok) {
            throw new IllegalStateException("ImageIO не смог записать JPG");
        }
    }
}
