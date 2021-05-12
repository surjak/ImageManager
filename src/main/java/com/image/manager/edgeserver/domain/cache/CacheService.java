package com.image.manager.edgeserver.domain.cache;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static com.image.manager.edgeserver.common.NumberUtils.getDigits;

@Service
@Slf4j
public class CacheService {

    private final Ehcache cache;
    private final String filePrefix;

    public CacheService(CacheManager cacheManager, CacheProperties cacheProperties) {
        this.cache = Optional.of(cacheManager)
                .map(manager -> manager.getCache(cacheProperties.getCacheName()))
                .map(EhCacheCache.class::cast)
                .map(EhCacheCache::getNativeCache)
                .orElseThrow(() -> new IllegalStateException("Could not find cache with name: " + cacheProperties.getCacheName()));

        this.filePrefix = cacheProperties.getFilePrefix();
    }

    public void purgeOne(int idx) {
        String keyPattern = String.format("%s%s.*", this.filePrefix, toIdxStr(idx));
        log.info("Attempting to remove cache for keys with pattern: {}", keyPattern);

        this.cache.getKeys()
                .stream()
                .map(Object::toString)
                .filter(key -> ((String) key).matches(keyPattern))
                .forEach(this.cache::remove);
    }

    public void purgeAll(Collection<Integer> idxs) {
        idxs.forEach(this::purgeOne);
    }

    private String toIdxStr(int idx) {
        return "0".repeat(12 - getDigits(idx)) + idx;
    }


}
