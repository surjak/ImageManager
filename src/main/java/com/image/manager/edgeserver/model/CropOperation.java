package com.image.manager.edgeserver.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class CropOperation extends Operation {

    //    private Integer x;
//    private Integer y;
//    private Integer w;
//    private Integer h;

//    public CropOperation(OperationType operationType, Integer x, Integer y, Integer w, Integer h) {
//        super(operationType);
//        this.x = x;
//        this.y = y;
//        this.w = w;
//        this.h = h;
//    }

    public CropOperation(OperationType operationType, Map<String, Integer> arguments) {
        super(operationType, arguments);
    }

    @Override
    public byte[] doOperation(byte[] image) {
        //TODO:
        return null;
    }
}
