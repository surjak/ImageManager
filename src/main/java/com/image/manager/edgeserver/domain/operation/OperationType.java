package com.image.manager.edgeserver.domain.operation;

import java.util.stream.Stream;

public enum OperationType {

    SCALE,
    CROP;

    public static OperationType getByName(String name) {
        return Stream.of(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation type"));
    }

}
