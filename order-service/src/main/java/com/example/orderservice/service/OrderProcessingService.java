package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.ValidationResult;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);
    private final InventoryServiceClient inventoryServiceClient;
    private final RateLimiter rateLimiter;

    public OrderProcessingService(InventoryServiceClient inventoryServiceClient, RateLimiter rateLimiter) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.rateLimiter = rateLimiter;
    }

    @Async("orderTaskExecutor")
    public CompletableFuture<ValidationResult> processOrder(OrderRequest order) {
        log.info("Thread {}: Waiting for Rate Limiter permission for order {}", Thread.currentThread().getName(), order.getOrderId());

        // The thread blocks here until a "permission" is obtained from the rate limiter.
        rateLimiter.acquire();

        log.info("Thread {}: Permission granted. Processing order {}", Thread.currentThread().getName(), order.getOrderId());

        try {
            boolean success = inventoryServiceClient.validateOrder(order);
            if (success) {
                return CompletableFuture.completedFuture(ValidationResult.success(order.getOrderId()));
            } else {
                return CompletableFuture.completedFuture(ValidationResult.failure(order.getOrderId()));
            }
        } catch (Exception e) {
            log.error("Unhandled exception during order processing {}: {}", order.getOrderId(), e.getMessage());
            return CompletableFuture.completedFuture(ValidationResult.failure(order.getOrderId()));
        }
    }
}