package com.image.manager.loadbalancer.router;


import com.image.manager.loadbalancer.resolver.RoutingResolver;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class RouterConfiguration {

    @Value("classpath:as.jpg")
    Resource resourceFile;

    @Bean
    @SneakyThrows
    public RouterFunction<ServerResponse> route(RoutingResolver resolver) {
        final byte[] bytes = resourceFile.getInputStream().readAllBytes();

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
                                            var edgeWebClient = resolver.resolve(a.getT2());

                                            return edgeWebClient
                                                    .map(webClient -> {
                                                        var client = webClient.getWebClient();
                                                        webClient.incrementCounter();
                                                        Flux<DataBuffer> mono = client.get()
                                                                .uri(a.getT2() + "?" + a.getT1())
                                                                .header("Host", a.getT3().firstHeader("Host"))
                                                                .accept(MediaType.APPLICATION_OCTET_STREAM)
                                                                .retrieve()
                                                                .bodyToFlux(DataBuffer.class);

                                                        return ok().contentType(MediaType.IMAGE_JPEG).body(mono, DataBuffer.class);
                                                    })
                                                    .orElseGet(() -> ok().contentType(MediaType.IMAGE_JPEG).body(Mono.just(bytes), byte[].class));
                                        }));
    }

}
