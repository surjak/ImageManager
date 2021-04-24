package com.image.manager.edgeserver.domain.operation;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Map;

@EqualsAndHashCode
public class ScaleOperation extends Operation {

    private Integer w;
    private Integer h;

    public ScaleOperation(Integer w, Integer h) {
        this.w = w;
        this.h = h;
    }

    @Override
    protected BufferedImage processImage(BufferedImage image) {
        fixMissingProperties(image);
        return Scalr.resize(image, Scalr.Mode.FIT_EXACT, w, h);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    protected void fixMissingProperties(BufferedImage image) {
        if (this.w == null && this.h == null) {
            this.w = image.getWidth();
            this.h = image.getHeight();
        }

        final var ratio = image.getWidth() / image.getHeight();

        this.w = this.w != null ? this.w : this.h * ratio;
        this.h = this.h != null ? this.h : this.w / ratio;
    }

    @Override
    protected void validateProperties(BufferedImage image) {
        Preconditions.checkArgument(w >= MIN_IMAGE_SIZE, "Target width cannot be negative");
        Preconditions.checkArgument(w <= MAX_IMAGE_SIZE, "Target width cannot be larger than 2^16");

        Preconditions.checkArgument(h >= MIN_IMAGE_SIZE, "Target height cannot be negative");
        Preconditions.checkArgument(h <= MAX_IMAGE_SIZE, "Target height cannot be larger than 2^16");
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
        public Operation fromArguments(Map<String, String> arguments) {
            Integer w = parseNumber(arguments.get(W));
            Integer h = parseNumber(arguments.get(H));

            return new ScaleOperation(w, h);
        }
    }

}
