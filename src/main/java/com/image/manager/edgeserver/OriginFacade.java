package com.image.manager.edgeserver;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${origin.host}")
    private String originUrl;

    public Mono<byte[]> fetchImageFromOrigin(String imageName) {
        return WebClient.create()
                .get()
                .uri(originUrl + "/" + imageName)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
