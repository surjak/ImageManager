package com.image.manager.edgeserver.model;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class ScaleOperation implements Operation {

    private final List<OperationArgument> arguments;

    public ScaleOperation(List<OperationArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public byte[] execute(byte[] image) {
        //TODO: implement this
        return null;
    }

    public List<OperationArgument> getArguments() {
        return arguments;
    }
}
