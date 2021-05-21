package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import com.image.manager.edgeserver.domain.operation.Operation;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;


/**
 * Created by surjak on 22.03.2021
 */
@Slf4j
public class OriginFacade {

    private final Map<String, Origin> origins;
    private final BufferedImageConverter imageConverter;
    private final DistributionSummary originInboundTraffic;
    private final DistributionSummary originOutboundTraffic;
    private final CacheManager cacheManager;
    private final Counter missCounter;
    private static final String CACHE_NAME = "edgeCache";

    public OriginFacade(
            CacheManager cacheManager,
            BufferedImageConverter imageConverter,
            List<Origin> origins,
            PrometheusMeterRegistry mr) {
        this.imageConverter = imageConverter;
        this.cacheManager = cacheManager;
        this.origins = origins.stream().collect(Collectors.toMap(Origin::getHostname, o -> o));
        this.originInboundTraffic = DistributionSummary
                .builder("origin.inbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);
        this.originOutboundTraffic = DistributionSummary
                .builder("edge.outbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);

        this.missCounter = Counter.builder("count.cache.miss").register(mr);
    }

    public Mono<ServerResponse> getImageAndApplyOperations(ServerRequest request, String fileName, List<Operation> operations) {
        var host = request
                .headers()
                .header("Host")
                .stream()
                .findFirst()
                .orElse(request.uri().getHost());

        String query = request.uri().getQuery();
        if (query == null) {
            query = "";
        }

        return fetchImageFromOrigin(host, fileName, query, operations)
                .flatMap(p -> ok().eTag(Optional.ofNullable(p.getETag()).orElse("default-etag")).cacheControl(CacheControl.maxAge(Duration.ofHours(1))).contentType(MediaType.IMAGE_PNG).body(Mono.justOrEmpty(p.getImage()), byte[].class));
    }

    @SneakyThrows
    public Mono<Origin.ResponseFromOrigin> fetchImageFromOrigin(String host, String imageName, String query, List<Operation> operations) {

        AtomicReference<Origin.ResponseFromOrigin> i = new AtomicReference<>();

        return Mono.justOrEmpty(Optional.ofNullable(cacheManager.getCache(CACHE_NAME).get(imageName + query, Origin.ResponseFromOrigin.class)))
                .switchIfEmpty(
                        Mono.justOrEmpty(Optional.ofNullable(cacheManager.getCache(CACHE_NAME).get(imageName, Origin.ResponseFromOrigin.class)))
                                .map(imgBytes -> {
                                    i.set(imgBytes);
                                    return imageConverter.byteArrayToBufferedImage(imgBytes.getImage());
                                })
                                .flatMap(img -> applyOperationsOnImage(operations, img, imageName))
                                .map(imageConverter::bufferedImageToByteArray)
                                .map(a -> {
                                    Origin.ResponseFromOrigin responseFromOrigin = new Origin.ResponseFromOrigin(a, i.get().getETag());
                                    if (!operations.isEmpty()) {
                                        cacheManager.getCache(CACHE_NAME)
                                                .put(imageName + query, responseFromOrigin);
                                    }
                                    return responseFromOrigin;
                                })
                ).switchIfEmpty(
                        Optional.of(Optional.ofNullable(this.origins.get(host))
                                .orElse(this.origins.values().iterator().next()))
                                .map(origin -> origin.fetchImageFromOrigin(imageName))
                                .map(result -> result.doOnSuccess(imgBytes -> {
                                            originInboundTraffic.record(imgBytes.getImage().length);
                                            missCounter.increment();
                                        }
                                ))
                                .orElse(Mono.error(new UnknownHostException("Origin host not found")))
                                .map(imgBytes -> {
                                    i.set(imgBytes);
                                    cacheManager.getCache(CACHE_NAME)
                                            .put(imageName, imgBytes);
                                    return imageConverter.byteArrayToBufferedImage(imgBytes.getImage());
                                })
                                .flatMap(img -> applyOperationsOnImage(operations, img, imageName))
                                .map(imageConverter::bufferedImageToByteArray)
                                .map(a -> new Origin.ResponseFromOrigin(a, i.get().getETag()))
                                .doOnSuccess(r -> {
                                    if (!operations.isEmpty()) {
                                        cacheManager.getCache(CACHE_NAME)
                                                .put(imageName + query, r);
                                    }
                                })
                ).doOnNext( r -> {
                     // TODO: 21.05.2021  remove additional thread pool???
                    originOutboundTraffic.record(r.getImage().length);
                });
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img, String fileName) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i, fileName));
    }
}
