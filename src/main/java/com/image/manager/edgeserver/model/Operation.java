package com.image.manager.edgeserver.model;

public interface Operation {
    byte[] execute(byte[] image);
}
