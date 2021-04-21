package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.application.config.cache.RocksDBRepository;
import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.stream.Collectors;

@Slf4j
@Configuration
public class OriginConfiguration {

    @Bean
    public OriginFacade originFacade(OriginProperties originProperties,
                                     RocksDBRepository rocksDBRepository,
                                     BufferedImageConverter imageConverter) {

        return new OriginFacade(
                rocksDBRepository,
                imageConverter,
                originProperties.getHosts()
                        .stream()
                        .map(host -> new Origin(host, initWebClient(host.getMaxConcurrentConnections())))
                        .collect(Collectors.toList())
        );
    }

    public WebClient initWebClient(int maxNumberOfConnections) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("connectionProvider").maxConnections(maxNumberOfConnections).build();
        HttpClient httpClient = HttpClient.create(connectionProvider);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build())
                .clientConnector(connector)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "origins")
    public OriginProperties originProperties() {
        return new OriginProperties();
    }

}
