package com.image.manager.loadbalancer.healthcheck;

import com.image.manager.loadbalancer.edgewebclient.EdgeWebClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HealthCheckService {

    private static final byte[] _fallback = {'x', 'd'};

    private final List<EdgeWebClient> clients;

    @Scheduled(fixedRate = 4349)
    public void checkClients() {
        clients.forEach(this::makeHealthCheckRequest);
    }

    public void makeHealthCheckRequest(EdgeWebClient client) {
        client.getWebClient()
                .get()
                .uri("/test/healthcheck")
                .retrieve()
                .bodyToMono(byte[].class)
                .timeout(Duration.ofSeconds(1))
                .doOnSuccess(result -> {
                    if(client.getStatus() == HealthStatus.UNHEALTHY) {
                        clients.forEach(EdgeWebClient::resetCounter);
                    }
                    client.setStatus(HealthStatus.HEALTHY);
                })
                .doOnError(error -> client.setStatus(HealthStatus.UNHEALTHY))
                .onErrorStop()
                .onErrorReturn(_fallback)
                .subscribe();
    }


}
