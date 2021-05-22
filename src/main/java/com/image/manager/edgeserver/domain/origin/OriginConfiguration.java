package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class OriginConfiguration {

    @Value("${client.maxInMemorySize}")
    private Integer maxInMemorySize;

    @Bean
    public OriginFacade originFacade(OriginProperties originProperties,
                                     BufferedImageConverter imageConverter,
                                     PrometheusMeterRegistry mr,
                                     CacheManager cacheManager
    ) {

        return new OriginFacade(
                cacheManager,
                imageConverter,
                originProperties
                        .getHosts()
                        .stream()
                        .map(host -> new Origin(host, initWebClient(host.getMaxConcurrentConnections())))
                        .collect(Collectors.toList()),
                mr);
    }

    public WebClient initWebClient(int maxNumberOfConnections) {
        System.out.println("Max in memory size: " + maxInMemorySize + " MB");
        ConnectionProvider connectionProvider = ConnectionProvider.builder("connectionProvider").maxConnections(maxNumberOfConnections)
                .pendingAcquireMaxCount(500)
                .pendingAcquireTimeout(Duration.ofSeconds(60)).build();
        HttpClient httpClient = HttpClient.create(connectionProvider);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(maxInMemorySize * 1024 * 1024))
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
