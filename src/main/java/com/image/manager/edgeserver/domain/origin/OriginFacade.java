package com.image.manager.edgeserver.domain.origin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.operation.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;


/**
 * Created by surjak on 22.03.2021
 */
@Service
public class OriginFacade {

    @Value("${origin.host}")
    private String originUrl;
    private final WebClient webClient;
    private final Cache<String, byte[]> requestCache;

    private final BufferedImageConverter imageConverter;

    public OriginFacade(BufferedImageConverter imageConverter) {
        webClient = WebClient.create();
        this.imageConverter = imageConverter;
        this.requestCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();

    }

    public Mono<byte[]> getImageAndApplyOperations(String fileName, List<Operation> operations) {
        return fetchImageFromOrigin(fileName)
                .map(imageConverter::byteArrayToBufferedImage)
                .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    public Mono<byte[]> fetchImageFromOrigin(String imageName) {
        byte[] bytes = requestCache.getIfPresent(imageName);
        if (bytes != null) {
            return Mono.just(bytes);
        }
        return webClient
                .get()
                .uri(originUrl + "/" + imageName)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(b -> requestCache.put(imageName, b));
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
