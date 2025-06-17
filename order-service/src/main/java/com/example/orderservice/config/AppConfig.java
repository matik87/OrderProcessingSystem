package com.example.orderservice.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Executor;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, @Value("${inventory.service.timeout.ms}") int timeoutMs) {
        return builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean(name = "orderTaskExecutor")
    public Executor taskExecutor(
            @Value("${order.processor.core-pool-size}") int corePoolSize,
            @Value("${order.processor.max-pool-size}") int maxPoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("OrderProcessor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public RateLimiter rateLimiter(@Value("${rate.limiter.permits-per-second}") double permitsPerSecond) {
        // Creates a RateLimiter that allows a configurable number of calls per second
        return RateLimiter.create(permitsPerSecond);
    }
}