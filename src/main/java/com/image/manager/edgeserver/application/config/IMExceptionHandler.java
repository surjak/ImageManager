package com.image.manager.edgeserver.application.config;

import com.image.manager.edgeserver.domain.ImageNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;


/**
 * Created by surjak on 14.04.2021
 */
@Component
@Order(-2)
public class IMExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        if (throwable instanceof ImageNotFoundException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return serverWebExchange.getResponse().setComplete();
        }
        if(throwable instanceof IllegalArgumentException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return serverWebExchange.getResponse().setComplete();
        }
        if(throwable instanceof Exception) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return serverWebExchange.getResponse().setComplete();
        }
        return Mono.error(throwable);
    }
}

