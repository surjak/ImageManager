package com.image.manager.edgeserver.domain.operation;

import lombok.EqualsAndHashCode;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.util.Map;

@EqualsAndHashCode
public class ScaleOperation implements Operation {
    public static final String W = "w";
    public static final String H = "h";
    private final Integer w;
    private final Integer h;

    public ScaleOperation(Integer w, Integer h) {
        this.w = w;
        this.h = h;
    }

    public static ScaleOperation fromArgumentsMap(Map<String, Integer> arguments) {
        Integer w = arguments.get(W);
        Integer h = arguments.get(H);
        return new ScaleOperation(w, h);
    }

    @Override
    public BufferedImage execute(BufferedImage image) {
        return Scalr.resize(image, w, h);
    }
}
