package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.ProcessResponse;
import com.example.orderservice.dto.ValidationResult;
import com.example.orderservice.service.OrderProcessingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderProcessingService orderProcessingService;

    public OrderController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping("/process")
    public ProcessResponse processOrders(@RequestBody List<OrderRequest> orders) {

        List<CompletableFuture<ValidationResult>> futures = orders.stream()
                .map(orderProcessingService::processOrder)
                .toList();

        // Wait for all asynchronous tasks to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Collect the results
        List<ValidationResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        long successfulCount = results.stream().filter(ValidationResult::isSuccess).count();
        List<String> failedOrderIds = results.stream()
                .filter(r -> !r.isSuccess())
                .map(ValidationResult::getOrderId)
                .collect(Collectors.toList());

        return ProcessResponse.builder()
                .totalOrders(orders.size())
                .successfulOrders(successfulCount)
                .failedOrders(failedOrderIds.size())
                .failedOrderIds(failedOrderIds)
                .build();
    }
}
