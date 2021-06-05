package com.image.manager.loadbalancer.resolver.cachedroundrobin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import com.image.manager.loadbalancer.resolver.AbstractRoutingResolver;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CachedRoundRobinRoutingResolver extends AbstractRoutingResolver {

    @Getter
    private final Map<String, CachedRequest> requestsByFilename = new HashMap<>();

    private final Multimap<EdgeWebClient, CachedRequest> requestsByClient = ArrayListMultimap.create();

    private int roundRobinCounter = 0;
    private final Counter catchCounter;

    public CachedRoundRobinRoutingResolver(List<EdgeWebClient> clients, PrometheusMeterRegistry mr) {
        super(clients);
        catchCounter = Counter.builder("count.random.client").register(mr);
    }

    @Override
    public Optional<EdgeWebClient> resolve(String filename) {
        var activeClients = super.getActiveClients();

        if(activeClients.isEmpty()) {
            return Optional.empty();
        }

        try {
            var cachedRequest = requestsByFilename.get(filename);
            if(cachedRequest == null) {
                cachedRequest = nextClient(filename, activeClients);
                requestsByFilename.put(filename, cachedRequest);
                requestsByClient.put(cachedRequest.getClient(), cachedRequest);
            } else {
                requestsByFilename.compute(filename, (k, v) -> v.incrementRequestCount());
            }

            return Optional.ofNullable(cachedRequest.getClient());
        } catch (Exception e) {
            catchCounter.increment();
            return Optional.ofNullable(nextClient(filename, activeClients).getClient());
        }

    }

    private CachedRequest nextClient(String filename, List<EdgeWebClient> activeClients) {
        return new CachedRequest(filename, activeClients.get((++roundRobinCounter) % activeClients.size()));
    }

    public List<CachedRequest> getRequestsByClient(EdgeWebClient client) {
        return new ArrayList<>(this.requestsByClient.get(client));
    }

    public void switchRequestsBetweenClients(List<CachedRequest> requestsToSwitch, EdgeWebClient from, EdgeWebClient to) {
        requestsToSwitch.forEach(request -> {
            request.setClient(to);
            this.requestsByClient.remove(from, request);
            this.requestsByClient.put(to, request);
        });
    }

    public void resetRequestCounters() {
        this.requestsByFilename.values().forEach(CachedRequest::resetRequestCount);
    }

}
