package com.image.manager.edgeserver.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class EdgeServerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleException(EdgeServerException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.toHttpStatus());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        return ex.getMessage();
    }
}
