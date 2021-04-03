package com.image.manager.edgeserver.domain.operation;

import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;
import java.util.Map;

@EqualsAndHashCode
public class CropOperation implements Operation {
    private static final String X = "x";
    private static final String Y = "y";
    private static final String W = "w";
    private static final String H = "h";

    private final Integer x;
    private final Integer y;
    private final Integer w;
    private final Integer h;

    public CropOperation(Integer x, Integer y, Integer w, Integer h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static CropOperation fromArgumentsMap(Map<String, Integer> arguments) {
        Integer x = arguments.get(X);
        Integer y = arguments.get(Y);
        Integer w = arguments.get(W);
        Integer h = arguments.get(H);
        return new CropOperation(x, y, w, h);
    }

    @Override
    public BufferedImage execute(BufferedImage image) {
        return image.getSubimage(x, y, w, h);
    }
}
