package com.image.manager.edgeserver.model;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class ScaleOperation implements Operation {

    private final Map<String, Integer> arguments;

    public ScaleOperation(Map<String, Integer> arguments) {
        this.arguments = arguments;
    }

    @Override
    public byte[] execute(byte[] image) {
        //TODO: implement this
        return null;
    }

    public Map<String, Integer> getArguments() {
        return arguments;
    }
}
