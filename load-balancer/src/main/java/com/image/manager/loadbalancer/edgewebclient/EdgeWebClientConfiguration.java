package com.image.manager.loadbalancer.edgewebclient;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class EdgeWebClientConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "edge")
    public EdgeWebClientProperties edgeWebClientProperties() {
        return new EdgeWebClientProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "client")
    public ClientConfig clientConfig() {
        return new ClientConfig();
    }



    @Bean
    public List<EdgeWebClient> edgeWebClients(EdgeWebClientProperties properties, PrometheusMeterRegistry mr, ClientConfig clientConfig) {
        return properties.getClientsIps()
                .stream()
                .map(s -> EdgeWebClient.fromHost(s, mr,
                        clientConfig.getMaxInMemorySize(),
                        clientConfig.getMaxConnections(),
                        clientConfig.getPendingAcquireMaxCount(),
                        clientConfig.getPendingAcquireTimeout()
                        ))
                .collect(Collectors.toList());
    }

}