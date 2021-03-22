package com.image.manager.edgeserver;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by surjak on 22.03.2021
 */
@Component
public class BufferedImageConverter {

    public byte[] bufferedImageToByteArray(BufferedImage image) {
        var stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public BufferedImage byteArrayToBufferedImage(byte[] bytes) {
        var stream = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
