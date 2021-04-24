package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.application.config.cache.RocksDBRepository;
import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.operation.Operation;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
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
    private final DistributionSummary originInboundTraffic;
    private final DistributionSummary originOutboundTraffic;

    public OriginFacade(
            RocksDBRepository rocksDBRepository,
            BufferedImageConverter imageConverter,
            List<Origin> origins,
            PrometheusMeterRegistry mr) {
        this.rocksDBRepository = rocksDBRepository;
        this.imageConverter = imageConverter;
        this.origins = origins.stream().collect(Collectors.toMap(Origin::getHost, o -> o));
        this.originInboundTraffic = DistributionSummary
                .builder("origin.inbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);
        this.originOutboundTraffic = DistributionSummary
                .builder("edge.outbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);
    }

    public Mono<byte[]> getImageAndApplyOperations(ServerRequest request, String fileName, List<Operation> operations) {
        var host = request
                .headers()
                .header("Host")
                .stream()
                .findFirst()
                .orElse(request.uri().getHost());

        return fetchImageFromOrigin(host, fileName)
                .map(imgBytes -> {
                    originOutboundTraffic.record(imgBytes.length);
                    return imageConverter.byteArrayToBufferedImage(imgBytes);
                })
                .flatMap(img -> applyOperationsOnImage(operations, img)).map(imageConverter::bufferedImageToByteArray);
    }

    @SneakyThrows
    public Mono<byte[]> fetchImageFromOrigin(String host, String imageName) {
        return rocksDBRepository.find(imageName).map(Mono::just).orElseGet(() -> Optional.ofNullable(this.origins.get(host))
                .map(origin -> origin.fetchImageFromOrigin(imageName))
                .map(result -> result.doOnSuccess(imgBytes -> {
                    originInboundTraffic.record(imgBytes.length);
                    rocksDBRepository.save(imageName, imgBytes);
                }))
                .orElse(Mono.error(new UnknownHostException("Origin host not found"))));
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i));
    }
}
