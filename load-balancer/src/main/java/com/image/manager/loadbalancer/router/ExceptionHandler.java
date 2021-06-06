package com.image.manager.loadbalancer.router;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * Created by surjak on 06.06.2021
 */
@Component
@Order(-2)
public class ExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ((Exception) throwable).printStackTrace();
        if (throwable instanceof Exception) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return serverWebExchange.getResponse().setComplete();
        }
        return Mono.error(throwable);
    }
}
