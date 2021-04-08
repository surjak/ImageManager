package com.image.manager.edgeserver.domain.operation;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface Operation {
    BufferedImage execute(BufferedImage image);

    interface Factory {

        OperationType getSupportedType();
        Operation fromArguments(Map<String, Integer> arguments);

    }

}
