package com.image.manager.loadbalancer.resolver.cachedroundrobin;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cache.Cache;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CachedRequest {

    @EqualsAndHashCode.Include
    private final String filename;
    private EdgeWebClient client;
    private int requestCount = 1;

    public CachedRequest(String filename, EdgeWebClient client) {
        this.filename = filename;
        this.client = client;
    }

    public CachedRequest incrementRequestCount() {
        this.requestCount++;
        return this;
    }

    public void resetRequestCount() {
        this.requestCount = 1;
    }

}
