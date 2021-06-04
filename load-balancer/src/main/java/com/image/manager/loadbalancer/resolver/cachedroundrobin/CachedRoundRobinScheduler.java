package com.image.manager.loadbalancer.resolver.cachedroundrobin;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import com.image.manager.loadbalancer.healthcheck.HealthStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.image.manager.loadbalancer.healthcheck.HealthStatus.HEALTHY;

@Component
@RequiredArgsConstructor
public class CachedRoundRobinScheduler {

    private static final Random r = new Random();

    private static final long REQUEST_THRESHOLD = 1000;
    private static final double REQUEST_RATIO_THRESHOLD = 0.1D;

    private static final Comparator<EdgeWebClient> byRequestCount = Comparator.comparing(EdgeWebClient::getRequestCount);
    private static final Supplier<TreeSet<EdgeWebClient>> toSortedSet = () -> new TreeSet<>(byRequestCount);

    private final CachedRoundRobinRoutingResolver resolver;
    private final List<EdgeWebClient> clients;

    @Scheduled(fixedRate = 10000)
    public void analyze() {
        var sorted = this.clients.stream()
                .collect(Collectors.toCollection(toSortedSet));

        var clientWithHighestRequestRate = sorted.last();
        var clientWithLowestRequestRate = sorted.first();

        long requestDiff = clientWithHighestRequestRate.getRequestCount() - clientWithLowestRequestRate.getRequestCount();

        double ratio = ((double) requestDiff) / clientWithLowestRequestRate.getRequestCount();
        if ((requestDiff > REQUEST_THRESHOLD || ratio > REQUEST_RATIO_THRESHOLD) && clientWithLowestRequestRate.getStatus() == HEALTHY) {
            var switchableRequests = resolver.getRequestsByClient(clientWithHighestRequestRate);

            var requestsToSwitch = this.getRequestsToSwitch(
                    switchableRequests,
                    switchableRequests.size(),
                    (int)(requestDiff / 2),
                    new LinkedList<>());

            this.resolver.switchRequestsBetweenClients(
                    requestsToSwitch,
                    clientWithHighestRequestRate,
                    clientWithLowestRequestRate
            );

            this.resolver.resetRequestCounters();
            this.clients.forEach(EdgeWebClient::resetCounter);
        }
    }

    private List<CachedRequest> getRequestsToSwitch(List<CachedRequest> switchableRequests,
                                                          int allRequestsToSwitchSize,
                                                          long restDiff,
                                                          List<CachedRequest> requestsToReturn) {
        if (restDiff < 0 || allRequestsToSwitchSize < 1) {
            return requestsToReturn;
        } else {
            var request = switchableRequests.remove(r.nextInt(allRequestsToSwitchSize));
            requestsToReturn.add(request);

            return getRequestsToSwitch(
                    switchableRequests,
                    --allRequestsToSwitchSize,
                    restDiff - request.getRequestCount(),
                    requestsToReturn
            );
        }
    }

}
