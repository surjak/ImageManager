package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;

import java.util.Optional;

public interface RoutingResolver {

    Optional<EdgeWebClient> resolve(String filename);

}
