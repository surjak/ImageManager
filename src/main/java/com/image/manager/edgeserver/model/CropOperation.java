package com.image.manager.edgeserver.model;

import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class CropOperation implements Operation {

    private final List<OperationArgument> arguments;

    public CropOperation(List<OperationArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public byte[] execute(byte[] image) {
        //TODO:
        return null;
    }

    public List<OperationArgument> getArguments() {
        return arguments;
    }
}
