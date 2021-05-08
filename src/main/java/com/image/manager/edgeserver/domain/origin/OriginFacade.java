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
import reactor.cache.CacheMono;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
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
    private final BiFunction<String, Signal<? extends Origin.ResponseFromOrigin>, Mono<Void>> writer;
    private final Function<String, Mono<Signal<? extends Origin.ResponseFromOrigin>>> reader;

    public OriginFacade(
            CacheManager cacheManager,
            BufferedImageConverter imageConverter,
            List<Origin> origins,
            PrometheusMeterRegistry mr) {
        this.imageConverter = imageConverter;
        this.cacheManager = cacheManager;
        this.origins = origins.stream().collect(Collectors.toMap(Origin::getHost, o -> o));
        this.originInboundTraffic = DistributionSummary
                .builder("origin.inbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);
        this.originOutboundTraffic = DistributionSummary
                .builder("edge.outbound.traffic.size")
                .baseUnit("bytes")
                .register(mr);
        this.writer = (k, val) -> Mono.just(val)
                .dematerialize()
                .doOnNext(l -> {
                    cacheManager.getCache(CACHE_NAME)
                            .put(k, l);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();

        this.reader = k -> Mono.justOrEmpty(
                Optional.ofNullable(cacheManager.getCache(CACHE_NAME).get(k, Origin.ResponseFromOrigin.class)))
                .subscribeOn(Schedulers.boundedElastic()) // to delete?
                .flatMap(v -> Mono.justOrEmpty(v).materialize())
        ;

        this.missCounter = Counter.builder("count.cache.miss").register(mr);
    }

    public Mono<ServerResponse> getImageAndApplyOperations(ServerRequest request, String fileName, List<Operation> operations) {
        var host = request
                .headers()
                .header("Host")
                .stream()
                .findFirst()
                .orElse(request.uri().getHost());
        AtomicReference<Origin.ResponseFromOrigin> i = new AtomicReference<>();
        String query = request.uri().getQuery();
        if(query == null) {
            query = "";
        }

        return fetchImageFromOrigin(host, fileName, query)
                .map(imgBytes -> {
                    i.set(imgBytes);
                    originOutboundTraffic.record(imgBytes.getImage().length);
                    return imageConverter.byteArrayToBufferedImage(imgBytes.getImage());
                })
                .flatMap(img -> applyOperationsOnImage(operations, img, fileName))
                .map(imageConverter::bufferedImageToByteArray)
                .map(a -> new Origin.ResponseFromOrigin(a, i.get().getETag()))
                .flatMap(p -> ok().eTag(Optional.ofNullable(p.getETag()).orElse("default-etag")).cacheControl(CacheControl.maxAge(Duration.ofHours(1))).contentType(MediaType.IMAGE_PNG).body(Mono.justOrEmpty(p.getImage()), byte[].class));
    }

    @SneakyThrows
    public Mono<Origin.ResponseFromOrigin> fetchImageFromOrigin(String host, String imageName, String query) {
        return CacheMono.lookup(reader, imageName + query)
                .onCacheMissResume(() ->
                          Optional.ofNullable(this.origins.get(host))
                                .map(origin -> origin.fetchImageFromOrigin(imageName))
                                .map(result -> result.doOnSuccess(imgBytes -> {
                                            originInboundTraffic.record(imgBytes.getImage().length);
                                            missCounter.increment();
                                            System.out.println(Thread.currentThread() + " From origin");
                                        }
                                ))
                                .orElse(Mono.error(new UnknownHostException("Origin host not found")))
                ).andWriteWith(writer);
    }

    private Mono<BufferedImage> applyOperationsOnImage(List<Operation> operations, BufferedImage img, String fileName) {
        return Flux.fromIterable(operations)
                .reduce(img, (i, operation) -> operation.execute(i, fileName));
    }
}
