package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;

public interface RoutingResolver {

    EdgeWebClient resolve(String filename);

}
