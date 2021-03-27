package com.image.manager.edgeserver.controllers;

import com.image.manager.edgeserver.BufferedImageConverter;
import com.image.manager.edgeserver.OriginFacade;
import com.image.manager.edgeserver.model.Operation;
import com.image.manager.edgeserver.model.OperationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Created by surjak on 25.03.2021
 */
@Configuration
public class RouterConfiguration {

    private final OriginFacade originFacade;
    private final OperationFactory operationFactory;
    private final BufferedImageConverter imageConverter;

    public RouterConfiguration(OriginFacade originFacade, OperationFactory operationFactory, BufferedImageConverter imageConverter) {
        this.originFacade = originFacade;
        this.operationFactory = operationFactory;
        this.imageConverter = imageConverter;
    }

    /**
     * Example usage for now:
     * http://localhost:8080/small-image.png?op=crop&w=50&h=50&x=50&y=50
     * http://localhost:8080/small-image.png?op=scale&w=700&h=700
     * http://localhost:8080/small-image.png?op=scale&w=700&h=700&op=crop&w=50&h=50&x=50&y=50 combo :)
     */
    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions
                .route(GET("/{fileName}"),
                        serverRequest -> {
                            List<Operation> operations = operationFactory.fromQuery(serverRequest.uri().getQuery());
                            String fileName = serverRequest.pathVariable("fileName");
                            return ok()
                                    .contentType(MediaType.IMAGE_PNG)
                                    .body(getImageAndApplyOperations(operations, fileName), byte[].class);
                        });
    }

    private Mono<byte[]> getImageAndApplyOperations(List<Operation> operations, String fileName) {
        return originFacade.fetchImageFromOrigin(fileName)
                                        .map(imageConverter::byteArrayToBufferedImage)
                                        .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
