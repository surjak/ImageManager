package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.ImageNotFoundException;
import com.image.manager.edgeserver.domain.operation.Operation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by surjak on 22.03.2021
 */

@Slf4j
public class OriginFacade {

    private final Map<String, Origin> origins;

    private final RedisTemplate<String, byte[]> redisTemplate;
    private final BufferedImageConverter imageConverter;

    public OriginFacade(RedisTemplate<String, byte[]> redisTemplate,
                        BufferedImageConverter imageConverter,
                        List<Origin> origins) {

        this.redisTemplate = redisTemplate;
        this.imageConverter = imageConverter;
        this.origins = origins.stream().collect(Collectors.toMap(Origin::getHost, o -> o));
    }

    public Mono<byte[]> getImageAndApplyOperations(String host, String fileName, List<Operation> operations) {

        return fetchImageFromOrigin(host, fileName)
                .map(imageConverter::byteArrayToBufferedImage)
                .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    @SneakyThrows
    public Mono<byte[]> fetchImageFromOrigin(String host, String imageName) {
        ValueOperations<String, byte[]> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(imageName).booleanValue()) {
            byte[] bytes = valueOperations.get(imageName);
            return Mono.just(bytes);
        }

        return Optional.ofNullable(this.origins.get(host))
                .map(origin -> origin.fetchImageFromOrigin(imageName))
                .map(result -> result.doOnSuccess(b -> valueOperations.set(imageName, b)))
                .orElse(Mono.error(new UnknownHostException("Origin host not found")));
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
