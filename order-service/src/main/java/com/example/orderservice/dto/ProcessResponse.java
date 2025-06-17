package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResponse {
    private long totalOrders;
    private long successfulOrders;
    private long failedOrders;
    private List<String> failedOrderIds;
}