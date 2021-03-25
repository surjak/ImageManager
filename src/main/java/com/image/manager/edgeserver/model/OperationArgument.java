package com.image.manager.edgeserver.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class OperationArgument {
    private final String key;
    private final Integer value;

    public OperationArgument(String key, Integer value) {
        this.key = key;
        this.value = value;
    }
}
