# Order Processing System - High-Level Design (HLD)

Kafka topic and consumer-group details are documented separately in [Kafka Topics and Consumer Groups](kafka-topics-and-consumer-groups.md).

## System Context

```mermaid
%%{init: {"themeVariables": {"fontSize": "18px"}, "flowchart": {"nodeSpacing": 56, "rankSpacing": 76, "diagramPadding": 24}}}%%
flowchart LR
    customer[Customer / Postman]

    subgraph services[Spring Boot Microservices]
        direction TB
        order[Order Service<br/>:8081]
        payment[Payment Service<br/>:8082]
        delivery[Delivery Service<br/>:8083]
        notification[Notification Service<br/>:8084]
    end

    subgraph kafka[Apache Kafka<br/>3 partitions per main topic]
        direction TB
        orders[(order-created)]
        success[(payment-success)]
        failed[(payment-failed)]
        deliveries[(delivery-created)]
        dlt[(payment-success.DLT)]
    end

    subgraph databases[Service-owned MySQL schemas]
        direction TB
        orderdb[(order_db)]
        paymentdb[(payment_db)]
        deliverydb[(delivery_db)]
    end

    customer -->|HTTP: POST /orders| order
    customer -->|HTTP: PATCH delivery status| delivery

    order -->|persist order| orderdb
    order -->|publish, key=orderId| orders
    orders -->|payment-service-group| payment
    orders -->|notification-order-group| notification

    payment -->|persist payment + idempotency key| paymentdb
    payment -->|success| success
    payment -->|failure| failed
    payment -.->|after initial attempt + 2 retries| dlt

    success -->|order-service-payment-group| order
    failed -->|order-service-payment-group| order
    success -->|delivery-service-group| delivery
    success -->|notification-payment-group| notification
    failed -->|notification-payment-group| notification

    delivery -->|persist delivery| deliverydb
    delivery -->|created and status events, key=orderId| deliveries
    deliveries -->|order-service-delivery-group| order
    deliveries -->|notification-service-group| notification

    classDef client fill:#fff4cc,stroke:#9a6b00,color:#252525
    classDef service fill:#e5f2ff,stroke:#1769aa,color:#10283e
    classDef topic fill:#e6f6ed,stroke:#27864b,color:#163323
    classDef deadletter fill:#ffe8e6,stroke:#b42318,color:#4a1712
    classDef database fill:#f0edff,stroke:#6554a4,color:#251e42
    class customer client
    class order,payment,delivery,notification service
    class orders,success,failed,deliveries topic
    class dlt deadletter
    class orderdb,paymentdb,deliverydb database
```

## Service Ownership

| Service | Owned data | Kafka responsibilities | HTTP interface |
| --- | --- | --- | --- |
| Order Service | Orders and order status | Produces `ORDER_CREATED`; consumes payment and delivery updates | `POST /orders` |
| Payment Service | Payments and processed event IDs | Consumes `order-created`; produces payment outcome; retries and dead-letters failures | None |
| Delivery Service | Delivery address, tracking number, status | Consumes successful payment; produces delivery lifecycle events | `PATCH /deliveries/{orderId}/status` |
| Notification Service | None | Independently consumes order, payment, and delivery events; prints simulated SMS/email | None |

## Reliability Notes

- Payment Service records processed event IDs and ignores repeated `ORDER_CREATED` messages. Delivery Service enforces one delivery per order.
- Payment handling retries twice after the initial attempt with a two-second fixed delay.
- Delivery status transitions are forward-only: `CREATED` to `IN_TRANSIT` or `CANCELLED`; `IN_TRANSIT` to `OUT_FOR_DELIVERY` or `CANCELLED`; `OUT_FOR_DELIVERY` to `DELIVERED` or `CANCELLED`. `DELIVERED` and `CANCELLED` are terminal.
- Databases are service-owned MySQL schemas. Kafka and MySQL are shared local infrastructure.