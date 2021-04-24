package com.image.manager.edgeserver.domain.operation;

import com.google.common.base.Preconditions;
import com.image.manager.edgeserver.domain.operation.parser.grammar.FormatOption;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CompressionOperation extends Operation {
    
    private final Integer compressionRate;

    @Override
    protected BufferedImage processImage(BufferedImage image, String imageFormat) {
        if(imageFormat.equals("jpg")) {
//            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            try {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(bao);
                writer.setOutput(ios);
                ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();
                imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imageWriteParam.setCompressionQuality((float) compressionRate/10);
//                imageWriteParam.setCompressionType(imageWriteParam.getCompressionTypes()[conversionInfo.getPng_rate()]);
                writer.write(null, new IIOImage(image, null, null), imageWriteParam);
//                writer.setOutput(ImageIO.createImageOutputStream(compressed));
//                ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();
//                imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                imageWriteParam.setCompressionQuality((float) compressionRate/10);
//                writer.write(null, new IIOImage(image, null, null), imageWriteParam);
                return ImageIO.read(new ByteArrayInputStream(bao.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
                //TODO:
                return image;
            }
        } else {
            return image;
        }
    }

    @Override
    protected void validateProperties(BufferedImage image) {
        Preconditions.checkArgument(compressionRate != null, "Invalid compression rate");
        Preconditions.checkArgument(compressionRate >=0 && compressionRate <=9, "Compression rate should be inside range [0-9]");
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
