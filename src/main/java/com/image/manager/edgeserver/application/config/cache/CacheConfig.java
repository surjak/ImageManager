package com.image.manager.edgeserver.application.config.cache;

import com.image.manager.edgeserver.domain.cache.CacheProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;

/**
 * Created by surjak on 01.05.2021
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager(@Value("${spring.cache.jcache.config}") String configPath) {
        EhCacheManagerFactoryBean managerFactoryBean = new EhCacheManagerFactoryBean();
        managerFactoryBean.setConfigLocation(new ClassPathResource(configPath));
        managerFactoryBean.setShared(true);
        return managerFactoryBean;
    }

    @Bean
    public CacheManager cacheManager(EhCacheManagerFactoryBean ehCacheManager) {
        return new EhCacheCacheManager(Objects.requireNonNull(ehCacheManager.getObject()));
    }

    @Qualifier("custom")
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("slow-");
        executor.initialize();
        return executor;
    }

    @Bean
    @ConfigurationProperties(prefix = "ehcache.properties")
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

}
