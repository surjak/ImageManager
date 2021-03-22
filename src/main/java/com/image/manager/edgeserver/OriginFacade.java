package com.image.manager.edgeserver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * Created by surjak on 22.03.2021
 */
@Service
public class OriginFacade {

    private final BufferedImageConverter converter;

    public OriginFacade(BufferedImageConverter converter) {
        this.converter = converter;
    }

    public Mono<byte[]> fetchImageFromOrigin() {
        return WebClient.create()
                .get()
                .uri("https://origin-server.herokuapp.com/small-image.png")
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(converter::byteArrayToBufferedImage)
                .map(image -> image.getSubimage(100, 100, 100, 100)) //service operation here
                .map(converter::bufferedImageToByteArray);
    }
}
