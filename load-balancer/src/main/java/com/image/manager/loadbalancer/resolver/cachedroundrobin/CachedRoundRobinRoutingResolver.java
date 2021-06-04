package com.image.manager.loadbalancer.resolver.cachedroundrobin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import com.image.manager.loadbalancer.resolver.AbstractRoutingResolver;
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

    public CachedRoundRobinRoutingResolver(List<EdgeWebClient> clients) {
        super(clients);
    }

    @Override
    public Optional<EdgeWebClient> resolve(String filename) {
        var activeClients = super.getActiveClients();

        if(activeClients.isEmpty()) {
            return Optional.empty();
        }

        var client = requestsByFilename.get(filename).getClient();
        if(client == null) {
            client = requestsByFilename.put(filename, nextClient(filename, activeClients)).getClient();
        } else {
            requestsByFilename.compute(filename, (k, v) -> v.incrementRequestCount());
        }

        return Optional.ofNullable(client);
    }

    private CachedRequest nextClient(String filename, List<EdgeWebClient> activeClients) {
        return new CachedRequest(filename, activeClients.get((++roundRobinCounter) % activeClients.size()), 1);
    }

    public List<CachedRequest> getRequestsByClient(EdgeWebClient client) {
        return new ArrayList<>(this.requestsByClient.get(client));
    }

    public void switchRequestsBetweenClients(List<CachedRequest> reuqestsToSwitch, EdgeWebClient from, EdgeWebClient to) {
        reuqestsToSwitch.forEach(request -> {
            request.setClient(to);
            this.requestsByClient.remove(from, request);
            this.requestsByClient.put(to, request);
        });
    }

}
