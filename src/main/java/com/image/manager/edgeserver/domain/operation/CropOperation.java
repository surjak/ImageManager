package com.image.manager.edgeserver.domain.operation;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class CropOperation extends Operation {

    private static final Integer DEFAULT_X = 0;
    private static final Integer DEFAULT_Y = 0;

    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;

    public CropOperation(Integer x, Integer y, Integer w, Integer h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    protected BufferedImage processImage(BufferedImage image, String imageFormat) {
        return image.getSubimage(x, y, w, h);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    protected void fixMissingProperties(BufferedImage image) {
        this.x = this.x != null ? this.x : DEFAULT_X;
        this.y = this.y != null ? this.y : DEFAULT_Y;

        this.w = this.w != null ? this.w : image.getWidth() - this.x;
        this.h = this.h != null ? this.h : image.getHeight() - this.y;
    }

    @Override
    protected void validateProperties(BufferedImage image) {
        Preconditions.checkArgument(w >= MIN_IMAGE_SIZE, "Target width cannot be negative");
        Preconditions.checkArgument(w <= MAX_IMAGE_SIZE, "Target width cannot be larger than 2^16");

        Preconditions.checkArgument(h >= MIN_IMAGE_SIZE, "Target height cannot be negative");
        Preconditions.checkArgument(h <= MAX_IMAGE_SIZE, "Target height cannot be larger than 2^16");

        Preconditions.checkArgument(x >= 0 && x <= image.getWidth(), "Origin point x is outside of original image bounds");
        Preconditions.checkArgument(y >= 0 && x <= image.getHeight(), "Origin point y is outside of original image bounds");

        final var wx = w + x;
        Preconditions.checkArgument(
                wx >= 0 && wx <= image.getWidth(),
                "Origin point moved by a vector (“w”, “h”) is outside of the original image bounds"
        );

        final var hy = h + y;
        Preconditions.checkArgument(
                hy >= 0 && hy <= image.getHeight(),
                "Origin point moved by a vector (“w”, “h”) is outside of the original image bounds"
        );

    }

    @Component
    public static class Factory implements Operation.Factory {

        private static final String X = "x";
        private static final String Y = "y";
        private static final String W = "w";
        private static final String H = "h";

        @Override
        public OperationType getSupportedType() {
            return OperationType.CROP;
        }

        @Override
        public Operation fromArguments(Map<String, String> arguments) {
            Integer x = parseNumber(arguments.get(X));
            Integer y = parseNumber(arguments.get(Y));
            Integer w = parseNumber(arguments.get(W));
            Integer h = parseNumber(arguments.get(H));

            return new CropOperation(x, y, w, h);
        }

    }

}
