package com.image.manager.edgeserver.controllers;

import com.image.manager.edgeserver.OriginFacade;
import com.image.manager.edgeserver.model.Operation;
import com.image.manager.edgeserver.model.OperationFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by surjak on 22.03.2021
 */
@RestController
public class ImageController {

    private final OriginFacade originFacade;

    public ImageController(OriginFacade originFacade) {
        this.originFacade = originFacade;
    }

    @GetMapping(value = "/{fileName}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Mono<byte[]> getImage(@PathVariable String fileName, HttpServletRequest request) {
        Mono<byte[]> image = originFacade.fetchImageFromOrigin(fileName);
        System.out.println(request.getQueryString());
        List<Operation> operations = OperationFactory.fromQuery(request.getQueryString());

        //TODO: run operations for image

        return image;
    }
}
