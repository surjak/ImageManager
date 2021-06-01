package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Deprecated
public class RoundRobinRoutingResolver extends AbstractRoutingResolver {

    private int routerInt = 0;

    public RoundRobinRoutingResolver(List<EdgeWebClient> clients) {
        super(clients);
    }

    @Override
    public Optional<EdgeWebClient> resolve(String filename) {
        return Optional.empty();
//        routerInt = (routerInt + 1) % size();
//        return Optional.ofNullable(clients.get(routerInt));
    }
}
