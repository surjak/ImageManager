package com.image.manager.edgeserver.model;

import java.awt.image.BufferedImage;

public interface Operation {
    BufferedImage execute(BufferedImage image);
}
