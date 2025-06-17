package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String orderId;
    private String productId;
    private int quantity;
}