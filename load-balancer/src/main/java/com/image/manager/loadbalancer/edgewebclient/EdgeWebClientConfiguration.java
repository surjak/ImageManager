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
    public List<EdgeWebClient> edgeWebClients(EdgeWebClientProperties properties, PrometheusMeterRegistry mr) {
        return properties.getClientsIps()
                .stream()
                .map(s -> EdgeWebClient.fromHost(s, mr))
                .collect(Collectors.toList());
    }

}