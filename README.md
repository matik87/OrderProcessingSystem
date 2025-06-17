# Distributed Order Processing System (Java/Spring Boot Solution)

This project contains the implementation of two microservices.

## Project Modules

1.  **inventory-service (App B):** A REST service that simulates inventory validation with erratic behavior.
2.  **order-service (App A):** A REST service that ingests batches of orders, processes them concurrently, and communicates with `inventory-service` by applying Rate Limiting and Retry policies.

## Prerequisites

- JDK 17 or higher.
- Maven 3.6 or higher.
- Ports 8081 and 8082 must be free.

## How to Run the Services

You must run both applications in separate terminals.

### 1. Run inventory-service (App B)

Navigate to the `inventory-service` folder and run:
```bash
mvn spring-boot:run
```
The service will start at `http://localhost:8081`.

### 2. Run order-service (App A)

Navigate to the `order-service` folder and run:
```bash
mvn spring-boot:run
```
The service will start at `http://localhost:8082`.

## How to Test the Solution

Once both services are running, you can send a POST request to the `order-service` to process a batch of orders.

Use a tool like `curl` or Postman.

**Example with `curl`:**
```bash
curl -X POST http://localhost:8082/api/v1/orders/process \
-H "Content-Type: application/json" \
-d '[
  {"orderId": "a1", "productId": "P1", "quantity": 1},
  {"orderId": "a2", "productId": "P2", "quantity": 2},
  {"orderId": "a3", "productId": "P3", "quantity": 1},
  {"orderId": "a4", "productId": "P4", "quantity": 5},
  {"orderId": "a5", "productId": "P5", "quantity": 1},
  {"orderId": "a6", "productId": "P6", "quantity": 2},
  {"orderId": "a7", "productId": "P7", "quantity": 3},
  {"orderId": "a8", "productId": "P8", "quantity": 1},
  {"orderId": "a9", "productId": "P9", "quantity": 4},
  {"orderId": "a10", "productId": "P10", "quantity": 1},
  {"orderId": "a11", "productId": "P11", "quantity": 1},
  {"orderId": "a12", "productId": "P12", "quantity": 2},
  {"orderId": "a13", "productId": "P13", "quantity": 1},
  {"orderId": "a14", "productId": "P14", "quantity": 5},
  {"orderId": "a15", "productId": "P15", "quantity": 1}
]'
```

**Expected Response:**
You will receive a summary indicating how many orders were processed successfully and which ones failed.

```json
{
    "totalOrders": 15,
    "successfulOrders": 11,
    "failedOrders": 4,
    "failedOrderIds": [
        "a3",
        "a10",
        "a7",
        "a15"
    ]
}
```
(The results will vary due to the random nature of App B).

-->