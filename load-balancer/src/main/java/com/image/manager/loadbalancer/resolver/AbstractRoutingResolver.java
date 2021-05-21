package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractRoutingResolver implements RoutingResolver {

    protected final List<EdgeWebClient> clients;
    protected final Map<String, EdgeWebClient> clientsByHost;

    protected AbstractRoutingResolver(List<EdgeWebClient> clients) {
        this.clients = clients;
        this.clientsByHost = clients.stream()
                .collect(Collectors.toMap(EdgeWebClient::getHost, e -> e));
    }

    protected final int size() {
        return this.clients.size();
    }

}
