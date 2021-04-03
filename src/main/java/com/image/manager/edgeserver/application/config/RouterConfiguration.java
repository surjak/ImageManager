package com.image.manager.edgeserver.application.config;

import com.image.manager.edgeserver.domain.origin.OriginFacade;
import com.image.manager.edgeserver.domain.operation.Operation;
import com.image.manager.edgeserver.domain.operation.OperationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

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

    public RouterConfiguration(OriginFacade originFacade, OperationFactory operationFactory) {
        this.originFacade = originFacade;
        this.operationFactory = operationFactory;
    }

    /**
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
                        serverRequest -> {
                            List<Operation> operations = operationFactory.fromQuery(serverRequest.uri().getQuery());
                            String fileName = serverRequest.pathVariable("fileName");
                            return ok()
                                    .contentType(MediaType.IMAGE_PNG)
                                    .body(originFacade.getImageAndApplyOperations(fileName, operations), byte[].class);
                        });
    }
}
