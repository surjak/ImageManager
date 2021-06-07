package com.image.manager.loadbalancer.edgewebclient;

import com.image.manager.loadbalancer.healthcheck.HealthStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Getter;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Getter
public class EdgeWebClient {

    public static EdgeWebClient fromHost(String host, PrometheusMeterRegistry mr,
                                         int maxInMemorySize,
                                         int maxNumberOfConnections,
                                         int pendingAcquireMaxCount,
                                         int pendingAcquireTimeout
    ) {
        System.out.println("maxInMemorySize: " + maxInMemorySize + " MB");
        System.out.println("maxNumberOfConnections: " + maxNumberOfConnections);
        System.out.println("pendingAcquireMaxCount: " + pendingAcquireMaxCount);
        System.out.println("pendingAcquireTimeout: " + pendingAcquireTimeout + " seconds");
        ConnectionProvider connectionProvider = ConnectionProvider.builder("connectionProvider")
                .maxConnections(maxNumberOfConnections)
                .pendingAcquireMaxCount(pendingAcquireMaxCount)
                .pendingAcquireTimeout(Duration.ofSeconds(pendingAcquireTimeout)).build();
        HttpClient httpClient = HttpClient.create(connectionProvider);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        WebClient webClient = WebClient.builder().baseUrl(host)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(maxInMemorySize * 1024 * 1024))
                        .build())
                .clientConnector(connector)
                .build();

        return new EdgeWebClient(host, webClient, mr);
    }

    private final String host;
    private final WebClient webClient;
    private final Counter missCounter;

    private long requestCount = 1;
    private HealthStatus status;

    private EdgeWebClient(String host, WebClient webClient, PrometheusMeterRegistry mr) {
        this.host = host;
        this.webClient = webClient;
        this.missCounter = Counter.builder("count.requests." + host).register(mr);
    }

    public void incrementCounter() {
        missCounter.increment();
        this.requestCount++;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    public void resetCounter() {
        this.requestCount = 1;
    }

    public boolean isActive() {
        return this.status == HealthStatus.HEALTHY;
    }

}
