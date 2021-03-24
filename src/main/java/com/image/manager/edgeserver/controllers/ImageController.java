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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by surjak on 22.03.2021
 */
@RestController
public class ImageController {

    private final OriginFacade originFacade;
//    private final ScaleService scaleService;
//    private final CropService cropService;

    public ImageController(OriginFacade originFacade) {
        this.originFacade = originFacade;
    }

    @GetMapping(value = "/{fileName}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Mono<byte[]> getImage(@PathVariable String fileName, HttpServletRequest request) {
        Mono<byte[]> image = originFacade.fetchImageFromOrigin(fileName);
        String params = request.getQueryString();
        if (params != null) {
            String[] operations = params.split("op=");
            List<Operation> operationList = new LinkedList<>();
            for (int i = 1; i < operations.length; i++) {
                operationList.add(OperationFactory.fromQuery(operations[i]));
            }
        }


        return image;
    }
}
