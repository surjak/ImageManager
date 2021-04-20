package com.image.manager.edgeserver.domain.origin;

import com.image.manager.edgeserver.common.converter.BufferedImageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class OriginConfiguration {

    @Bean
    public OriginFacade originFacade(OriginProperties originProperties,
                                     RedisTemplate<String, byte[]> redisTemplate,
                                     BufferedImageConverter imageConverter,
                                     WebClient webClient) {
        return new OriginFacade(
                redisTemplate,
                imageConverter,
                originProperties.getHosts()
                        .stream()
                        .map(host -> new Origin(host, webClient))
                        .collect(Collectors.toList())
        );
    }

    @Bean
    @ConfigurationProperties(prefix = "origins")
    public OriginProperties originProperties() {
        return new OriginProperties();
    }

}
