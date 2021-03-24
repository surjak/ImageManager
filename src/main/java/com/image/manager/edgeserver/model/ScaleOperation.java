package com.image.manager.edgeserver.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScaleOperation extends Operation {
//    private Integer w;
//    private Integer h;
//
//    public ScaleOperation(OperationType operationType, Integer w, Integer h) {
//        super(operationType);
//        this.w = w;
//        this.h = h;
//    }

    public ScaleOperation(OperationType operationType, Map<String, Integer> arguments) {
        super(operationType, arguments);
    }

    @Override
    public byte[] doOperation(byte[] image) {
        //TODO: implement this
        return null;
    }
}
