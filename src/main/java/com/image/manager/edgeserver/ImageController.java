package com.image.manager.edgeserver;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Created by surjak on 22.03.2021
 */
@RestController
public class ImageController {
    private final OriginFacade originFacade;

    public ImageController(OriginFacade originFacade) {
        this.originFacade = originFacade;
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> getImage() {
        return originFacade.fetchImageFromOrigin();
    }
}
