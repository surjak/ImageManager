package com.image.manager.edgeserver.domain.operation;

import java.awt.image.BufferedImage;
import java.util.Map;

public abstract class Operation {

    protected static final Integer MIN_IMAGE_SIZE = 0;
    protected static final Integer MAX_IMAGE_SIZE = 65536;

    protected abstract BufferedImage processImage(BufferedImage image, String imageFormat);

    public abstract int getOrder();

    public final BufferedImage execute(BufferedImage image, String fileName) {
        fixMissingProperties(image);
        validateProperties(image);
        var strings =  fileName.split("\\.");

        return processImage(image, strings[strings.length - 1]);
    }

    protected void fixMissingProperties(BufferedImage image) {

    }

    protected void validateProperties(BufferedImage image) {

    }

    interface Factory {
        OperationType getSupportedType();
        Operation fromArguments(Map<String, String> arguments);
    }

    protected static Integer parseNumber(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
