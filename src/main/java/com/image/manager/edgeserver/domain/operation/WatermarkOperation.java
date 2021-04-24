package com.image.manager.edgeserver.domain.operation;

import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

@EqualsAndHashCode
public class WatermarkOperation extends Operation {


    public WatermarkOperation() {
    }

    @Override
    protected BufferedImage processImage(BufferedImage image) {
        Graphics2D graphics2D = (Graphics2D)image.getGraphics();
        graphics2D.setFont(new Font("Arial", Font.BOLD, 10));
        graphics2D.drawString("AGH Copyright @2021", image.getWidth() / 2, image.getHeight() / 2);
        return image;
    }

    @Component
    public static class Factory implements Operation.Factory {
        @Override
        public OperationType getSupportedType() {
            return OperationType.WATERMARK;
        }

        @Override
        public Operation fromArguments(Map<String, Integer> arguments) {
            return new WatermarkOperation();
        }
    }

}
