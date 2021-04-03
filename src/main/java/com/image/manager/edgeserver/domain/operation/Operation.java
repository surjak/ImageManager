package com.image.manager.edgeserver.domain.operation;

import java.awt.image.BufferedImage;

public interface Operation {
    BufferedImage execute(BufferedImage image);
}
