package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.domain.ImageNotFoundException;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.Serializable;
import java.net.URI;

public class Origin {

    @Getter
    private final String hostname;

    @Getter
    private final String ip;

    @Getter
    private final int maxConcurrentConnections;
    private final WebClient webClient;
//    ThreadPoolTaskExecutor taskExecutor;
    public Origin(OriginProperties.OriginHost host, WebClient webClient) {
        this.hostname = host.getHostname();
        this.ip = host.getIp();
        this.maxConcurrentConnections = host.getMaxConcurrentConnections();
        this.webClient = webClient;
        this.taskExecutor = taskExecutor;
    }

    @SneakyThrows
    public Mono<ResponseFromOrigin> fetchImageFromOrigin(String imageName) {
        return webClient
                .get()
//                .uri(new URI(host + "/" + imageName.trim()))
                .uri(new URI("10.0.0.102" + "/" + imageName.trim()))
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is4xxClientError()) {
                        return Mono.error(new ImageNotFoundException("Image not found"));
                    } else {
                        String etag = clientResponse.headers().asHttpHeaders().getETag();
                        return clientResponse.bodyToMono(byte[].class).map(im -> new ResponseFromOrigin(im, etag));
                    }
                }).publishOn(Schedulers.fromExecutor(taskExecutor));
    }

    @Data
    public static class ResponseFromOrigin implements Serializable {
        private final byte[] image;
        private final String eTag;
    }


}
