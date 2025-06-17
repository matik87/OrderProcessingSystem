package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private String orderId;
    private String productId;
    private int quantity;
}
