package com.image.manager.edgeserver.api;

import com.image.manager.edgeserver.domain.cache.CacheService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeCache(@RequestBody PurgeCacheRequest purgeCacheRequest) {
        this.cacheService.purgeAll(purgeCacheRequest.toIdxs());
    }

    @Data
    private static class PurgeCacheRequest {

        private List<Integer> keys;
        private List<KeyRange> keyRanges;

        public Collection<Integer> toIdxs() {
            Stream<Integer> keys = this.keys.stream();
            Stream<Integer> keyRanges = this.keyRanges.stream().flatMap(KeyRange::toRange);

            return Stream.concat(keys, keyRanges)
                    .distinct()
                    .collect(Collectors.toList());
        }

        @Data
        private static class KeyRange {

            private int from;
            private int to;

            public Stream<Integer> toRange() {
                return IntStream.rangeClosed(this.from, this.to).boxed();
            }

        }

    }

}
