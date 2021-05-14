package com.image.manager.edgeserver.api;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
public class PurgeCacheRequest {

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
            return IntStream.range(this.from, this.to).boxed();
        }

    }

}
