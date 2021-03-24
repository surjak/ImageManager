package com.image.manager.edgeserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public abstract class Operation {
    private OperationType operationType;
    private Map<String, Integer> arguments;

    public Operation() {
    }

    public abstract byte[] doOperation(byte[] image);
}
