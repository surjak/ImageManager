package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.domain.ImageNotFoundException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

public class Origin {

    @Getter
    private final String host;
    @Getter
    private final int maxConcurrentConnections;
    private final WebClient webClient;

    public Origin(OriginProperties.OriginHost host, WebClient webClient) {
        this.host = host.getUrl();
        this.maxConcurrentConnections = host.getMaxConcurrentConnections();
        this.webClient = webClient;
    }

    @SneakyThrows
    public Mono<byte[]> fetchImageFromOrigin(String imageName) {
        return webClient
                .get()
                .uri(new URI(host + "/" + imageName.trim()))
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new ImageNotFoundException("Image not found")))
                .bodyToMono(byte[].class);
    }

}
