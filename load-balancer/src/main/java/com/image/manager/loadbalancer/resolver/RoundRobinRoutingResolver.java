package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoundRobinRoutingResolver extends AbstractRoutingResolver {

    private int routerInt = 0;

    public RoundRobinRoutingResolver(List<EdgeWebClient> clients) {
        super(clients);
    }

    @Override
    public EdgeWebClient resolve(String filename) {
        routerInt = (routerInt + 1) % size();
        return clients.get(routerInt);
    }
}
