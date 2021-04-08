package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.operation.Operation;
import org.springframework.beans.factory.annotation.Value;
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

    private final BufferedImageConverter imageConverter;

    public OriginFacade(BufferedImageConverter imageConverter) {
        this.imageConverter = imageConverter;
    }

    public Mono<byte[]> getImageAndApplyOperations(String fileName, List<Operation> operations) {
        return fetchImageFromOrigin(fileName)
                .map(imageConverter::byteArrayToBufferedImage)
                .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    public Mono<byte[]> fetchImageFromOrigin(String imageName) {
        return WebClient.create()
                .get()
                .uri(originUrl + "/" + imageName)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
