package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClient.class);
    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public InventoryServiceClient(RestTemplate restTemplate, @Value("${inventory.service.url}") String inventoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    @Retryable(
            value = { HttpServerErrorException.class, ResourceAccessException.class }, // Retry on 5xx or I/O errors (timeout)
            maxAttemptsExpression = "${retry.max-attempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.initial-delay-ms}",
                    multiplierExpression = "${retry.multiplier}"
            )
    )
    public boolean validateOrder(OrderRequest order) {
        log.info("Trying to validate the order: {}", order.getOrderId());
        ResponseEntity<String> response = restTemplate.postForEntity(inventoryServiceUrl, order, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Recover
    public boolean recoverFromValidationFailure(Exception e, OrderRequest order) {
        // This method is called when @Retryable exhausts all its attempts.
        log.error("Validation failed for order {} after multiple attempts. Cause: {}", order.getOrderId(), e.getMessage());
        return false;
    }
}