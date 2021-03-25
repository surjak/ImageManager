package com.image.manager.edgeserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

public interface Operation {
//    private Map<String, Integer> arguments;
//
//    public Operation(OperationType operationType) {
//        this.operationType = operationType;
//    }
//
//    public Operation() {
//    }

    byte[] execute(byte[] image);
}
