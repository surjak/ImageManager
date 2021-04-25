package com.image.manager.edgeserver.domain.operation;

import com.google.common.base.Preconditions;
import com.image.manager.edgeserver.domain.operation.parser.grammar.FormatOption;
import com.pngencoder.PngEncoder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CompressionOperation extends Operation {

    private final Integer compressionRate;

    @Override
    protected BufferedImage processImage(BufferedImage image, String imageFormat) {
        if (imageFormat.equals("png")) {
            try {
                return ImageIO.read(
                        new ByteArrayInputStream(
                        new PngEncoder()
                        .withBufferedImage(image)
                        .withCompressionLevel(compressionRate)
                        .toBytes()));
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: handle exceptions
                return image;
            }
        } else {
            return image;
        }
    }

    @Override
    protected void validateProperties(BufferedImage image) {
        Preconditions.checkArgument(compressionRate != null, "Invalid compression rate");
        Preconditions.checkArgument(compressionRate >= 0 && compressionRate <= 9, "Compression rate should be inside range [0-9]");
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Component
    public static class Factory implements Operation.Factory {

        @Override
        public OperationType getSupportedType() {
            return OperationType.COMPRESSION;
        }

        @Override
        public Operation fromArguments(Map<String, String> arguments) {
            return new CompressionOperation(parseNumber(arguments.get(FormatOption.VALUE)));
        }

    }
}
