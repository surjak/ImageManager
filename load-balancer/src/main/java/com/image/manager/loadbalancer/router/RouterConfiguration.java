package com.image.manager.loadbalancer.router;


import com.image.manager.loadbalancer.resolver.RoutingResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public RouterFunction<ServerResponse> route(RoutingResolver resolver) {
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
                                            var client = edgeWebClient.getWebClient();
                                            edgeWebClient.incrementCounter();
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
