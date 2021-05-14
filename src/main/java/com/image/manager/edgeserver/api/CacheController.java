package com.image.manager.edgeserver.api;

import com.image.manager.edgeserver.domain.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
