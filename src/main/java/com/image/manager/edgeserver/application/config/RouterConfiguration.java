package com.image.manager.edgeserver.application.config;

import com.image.manager.edgeserver.domain.operation.parser.OperationParser;
import com.image.manager.edgeserver.domain.origin.OriginFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Created by surjak on 25.03.2021
 */
@Slf4j
@Configuration
public class RouterConfiguration {

    private final OriginFacade originFacade;
    private final OperationParser operationParser;

    public RouterConfiguration(OriginFacade originFacade, OperationParser operationParser) {
        this.originFacade = originFacade;
        this.operationParser = operationParser;
    }

    /**
     * curl -v "3.64.252.146:8080/COCO_train2014_000000061203.jpg" --resolve "3.64.252.146:8080:127.0.0.1"
     * <p>
     * Example usage for now:
     * http://localhost:8080/small-image.png
     * http://localhost:8080/small-image.png?op=crop&w=50&h=50&x=50&y=50
     * http://localhost:8080/small-image.png?op=scale&w=700&h=700
     * http://localhost:8080/small-image.png?op=scale&w=700&h=700&op=crop&w=50&h=50&x=50&y=50 combo :)
     */
    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions
                .route(GET("/{fileName}"),
                        serverRequest ->
                                Mono.zip(
                                        Mono.justOrEmpty(serverRequest.uri().getQuery())
                                                .map(operationParser::fromQuery)
                                                .switchIfEmpty(Mono.just(java.util.List.of())),
                                        Mono.justOrEmpty(serverRequest.pathVariable("fileName")),
                                        Mono.justOrEmpty(serverRequest.uri().getHost())
                                )
                                        .map(a -> originFacade.getImageAndApplyOperations(a.getT3(), a.getT2(), a.getT1()))
                                        .flatMap(p -> ok().contentType(MediaType.IMAGE_PNG).body(p, byte[].class))

                );
    }
}
