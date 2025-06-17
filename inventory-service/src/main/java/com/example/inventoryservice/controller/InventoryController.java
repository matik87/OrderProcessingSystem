package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryRequest;
import com.example.inventoryservice.dto.InventoryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @PostMapping("/validate")
    public ResponseEntity<InventoryResponse> validateInventory(@RequestBody InventoryRequest request) throws InterruptedException {
        log.info("Validating inventory for the order: {}", request.getOrderId());

        int behavior = ThreadLocalRandom.current().nextInt(1, 101); // 1 to 100

        // 70% success
        if (behavior <= 70) {
            int delay = ThreadLocalRandom.current().nextInt(100, 301);
            TimeUnit.MILLISECONDS.sleep(delay);
            log.info("Order {} validated successfully.", request.getOrderId());
            InventoryResponse response = new InventoryResponse(request.getOrderId(), "INVENTORY_VALIDATED");
            return ResponseEntity.ok(response);
        }
        // 20% server error
        else if (behavior <= 90) {
            log.warn("Simulating server error for the order: {}", request.getOrderId());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        // 10% timeout
        else {
            log.warn("Simulating timeout for the order: {}", request.getOrderId());
            TimeUnit.SECONDS.sleep(3); // This should cause a timeout on the client.
            // The answer here doesn't matter that much, the client should cut the connection first.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}