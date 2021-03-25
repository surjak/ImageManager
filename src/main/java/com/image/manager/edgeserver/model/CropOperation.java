package com.image.manager.edgeserver.model;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class CropOperation implements Operation {

    private final Map<String, Integer> arguments;

    public CropOperation(Map<String, Integer> arguments) {
        this.arguments = arguments;
    }

    @Override
    public byte[] execute(byte[] image) {
        //TODO:
        return null;
    }

    public Map<String, Integer> getArguments() {
        return arguments;
    }
}
