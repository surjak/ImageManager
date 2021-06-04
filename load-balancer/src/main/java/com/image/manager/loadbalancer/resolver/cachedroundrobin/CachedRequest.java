package com.image.manager.loadbalancer.resolver.cachedroundrobin;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cache.Cache;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CachedRequest {

    @EqualsAndHashCode.Include
    private final String filename;
    private EdgeWebClient client;
    private int requestCount;

    public CachedRequest incrementRequestCount() {
        this.requestCount++;
        return this;
    }

}
