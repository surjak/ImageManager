package com.image.manager.edgeserver.domain.operation;

import lombok.EqualsAndHashCode;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Map;

@EqualsAndHashCode
public class ScaleOperation implements Operation {

    private final Integer w;
    private final Integer h;

    public ScaleOperation(Integer w, Integer h) {
        this.w = w;
        this.h = h;
    }

    @Override
    public BufferedImage execute(BufferedImage image) {
        return Scalr.resize(image, w, h);
    }

    @Component
    public static class Factory implements Operation.Factory {

        private static final String W = "w";
        private static final String H = "h";

        @Override
        public OperationType getSupportedType() {
            return OperationType.SCALE;
        }

        @Override
        public Operation fromArguments(Map<String, Integer> arguments) {
            Integer w = arguments.get(W);
            Integer h = arguments.get(H);

            return new ScaleOperation(w, h);
        }
    }

}
