package com.image.manager.edgeserver.domain.operation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OperationType {

    SCALE("scale"),
    CROP("crop"),
    WATERMARK("w");

    private final String alias;

    public static OperationType getByName(String name) {
        return Stream.of(values())
                .filter(e -> e.alias.equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation type"));
    }

}
