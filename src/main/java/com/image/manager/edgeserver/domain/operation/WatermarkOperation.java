package com.image.manager.edgeserver.domain.operation;

import com.image.manager.edgeserver.domain.operation.parser.grammar.FormatOption;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class WatermarkOperation extends Operation {


    public static final String AGH_COPYRIGHT_2021 = "AGH Copyright @2021";

    @Override
    protected BufferedImage processImage(BufferedImage image) {
        Graphics graphics = image.getGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 20));
        graphics.drawString(AGH_COPYRIGHT_2021, image.getWidth() / 2, image.getHeight() / 2);
        graphics.dispose();
        return image;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Component
    public static class Factory implements Operation.Factory {

        @Override
        public OperationType getSupportedType() {
            return OperationType.WATERMARK;
        }

        @Override
        public Operation fromArguments(Map<String, String> arguments) {
            return arguments.containsKey(FormatOption.VALUE) ? new WatermarkOperation() : null;
        }

    }
}
