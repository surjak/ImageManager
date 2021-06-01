package com.image.manager.loadbalancer.edgewebclient;

import com.image.manager.loadbalancer.healthcheck.HealthStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class EdgeWebClient {

    public static EdgeWebClient fromHost(String host, PrometheusMeterRegistry mr) {
        return new EdgeWebClient(host, WebClient.create(host), mr);
    }

    private final String host;
    private final WebClient webClient;
    private final Counter missCounter;

    private long totalRequests;
    private HealthStatus status;

    private EdgeWebClient(String host, WebClient webClient, PrometheusMeterRegistry mr) {
        this.host = host;
        this.webClient = webClient;
        this.missCounter = Counter.builder("count.requests." + host).register(mr);
    }

    public void incrementCounter() {
        missCounter.increment();
        this.totalRequests++;
    }

    public void setStatus(HealthStatus status) {
        System.out.printf("Client related to host %s -> %s\n", this.host, status.name());
        this.status = status;
    }

    public void resetCounter() {
        this.totalRequests = 0;
    }

    public boolean isActive() {
        return this.status == HealthStatus.HEALTHY;
    }

}
