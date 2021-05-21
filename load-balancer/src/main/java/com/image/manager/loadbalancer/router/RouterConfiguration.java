package com.image.manager.loadbalancer.router;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

import static org.springframework.web.reactive.function.server.ServerResponse.accepted;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class RouterConfiguration {
    Random random = new Random(2);

    @Bean
    @EdgeWebClient
    public WebClient edge1WebClient() {
        return WebClient.create("http://localhost:11000");
    }

    @Bean
    @EdgeWebClient
    WebClient edge2WebClient() {
        return WebClient.create("http://localhost:12000");
    }

    @Bean
    public RouterFunction<ServerResponse> route(@EdgeWebClient List<WebClient> clients) {
        return RouterFunctions
                .route(GET("/{fileName}"),
                        serverRequest ->
                                Mono.zip(
                                        Mono.justOrEmpty(serverRequest.uri().getQuery())
                                                .switchIfEmpty(Mono.just("")),
                                        Mono.justOrEmpty(serverRequest.pathVariable("fileName")),
                                        Mono.just(serverRequest.headers())
                                )
                                        .flatMap(a -> {
                                            var client = clients.get(random.nextInt(2));

                                            System.out.println("client: " + client.toString());

                                            Flux<DataBuffer> mono = client.get()
                                                    .uri(a.getT2() + "?" + a.getT1())
                                                    .header("Host", a.getT3().firstHeader("Host"))
                                                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                                                    .retrieve()
                                                    .bodyToFlux(DataBuffer.class);

                                            return ok().contentType(MediaType.IMAGE_JPEG).body(mono, DataBuffer.class);
                                        }));
    }

}
