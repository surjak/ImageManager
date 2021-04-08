package com.image.manager.edgeserver.api;

import org.springframework.http.HttpStatus;

public abstract class EdgeServerException extends RuntimeException {
    public EdgeServerException(String message) {
        super(message);
    }

    public EdgeServerException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract HttpStatus toHttpStatus();
}
