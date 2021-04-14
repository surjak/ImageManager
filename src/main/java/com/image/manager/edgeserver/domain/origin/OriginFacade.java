package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.ImageNotFoundException;
import com.image.manager.edgeserver.domain.operation.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.util.List;


/**
 * Created by surjak on 22.03.2021
 */
@Service
public class OriginFacade {

    @Value("${origin.host}")
    private String originUrl;
    private final WebClient webClient;
    private final RedisTemplate<String, byte[]> redisTemplate;

    private final BufferedImageConverter imageConverter;

    public OriginFacade(RedisTemplate<String, byte[]> redisTemplate, BufferedImageConverter imageConverter) {
        this.redisTemplate = redisTemplate;
        webClient = WebClient.create();
        this.imageConverter = imageConverter;
    }

    public Mono<byte[]> getImageAndApplyOperations(String fileName, List<Operation> operations) {
        return fetchImageFromOrigin(fileName)
                .map(imageConverter::byteArrayToBufferedImage)
                .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    public Mono<byte[]> fetchImageFromOrigin(String imageName) {
        ValueOperations<String, byte[]> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(imageName).booleanValue()) {
            byte[] bytes = valueOperations.get(imageName);
            return Mono.just(bytes);
        }
        return webClient
                .get()
                .uri(originUrl + "/" + imageName)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new ImageNotFoundException("Image not found"))
                )
                .bodyToMono(byte[].class)
                .doOnSuccess(b -> valueOperations.set(imageName, b));
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
