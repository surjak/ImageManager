package com.image.manager.edgeserver.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class TestRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> testRoute() {
        return RouterFunctions.route(GET("/test/healthcheck"), serverRequest -> {
            return ok().body(BodyInserters.fromValue("Hello World"));
        });
    }

}
