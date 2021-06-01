package com.image.manager.loadbalancer.resolver;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VeryAdvancedAndEfficientPoweredByAIAndSchedulingInnovativeTasksRoutingResolverXD extends AbstractRoutingResolver {

    private final Map<String, EdgeWebClient> clientsByFilename = new HashMap<>();

    public VeryAdvancedAndEfficientPoweredByAIAndSchedulingInnovativeTasksRoutingResolverXD(List<EdgeWebClient> clients) {
        super(clients);
    }

    @Override
    public Optional<EdgeWebClient> resolve(String filename) {
        var activeClients = super.getActiveClients();

        if(activeClients.isEmpty()) {
            return Optional.empty();
        }

        var client = clientsByFilename.putIfAbsent(filename, activeClients.get(filename.hashCode() % activeClients.size()));

        activeClients.stream()
                .filter(c -> c != client)
                .filter(c -> client.getTotalRequests() - c.getTotalRequests() > 1000)
                .findAny()
                .ifPresent(c -> clientsByFilename.put(filename, c));

        return Optional.of(client);
    }


}
