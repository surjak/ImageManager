package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.application.config.cache.RocksDBRepository;
import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.operation.Operation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
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

    private final BufferedImageConverter imageConverter;

    private final RocksDBRepository rocksDBRepository;

    public OriginFacade(
            RocksDBRepository rocksDBRepository,
            BufferedImageConverter imageConverter,
            List<Origin> origins) {
        this.rocksDBRepository = rocksDBRepository;
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
        return rocksDBRepository.find(imageName).map(Mono::just).orElseGet(() -> Optional.ofNullable(this.origins.get(host))
                .map(origin -> origin.fetchImageFromOrigin(imageName))
                .map(result -> result.doOnSuccess(b -> rocksDBRepository.save(imageName, b)))
                .orElse(Mono.error(new UnknownHostException("Origin host not found"))));
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
