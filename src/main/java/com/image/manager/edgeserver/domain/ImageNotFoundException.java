package com.image.manager.edgeserver.domain;

/**
 * Created by surjak on 14.04.2021
 */
public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(String message) {
        super(message);
    }
}
