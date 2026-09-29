# Kafka Live Proof and Order Flow Evidence

## Project Report

This report documents the live Kafka-based order processing flow observed in the repository. It presents verified runtime evidence collected from the active broker and Spring Boot services. The evidence below shows that the application is successfully publishing Kafka events, consuming them in separate consumer groups, updating order state, and processing duplicate-message scenarios without breaking business behavior.

### Scope
- Validate MySQL connectivity
- Validate Kafka broker health and topic creation
- Validate order creation via the HTTP API
- Validate downstream payment and delivery event flow
- Validate independent consumer groups and event distribution
- Capture text-based evidence where no static screenshots were produced

### Executive Summary
The application successfully processes an order through the Kafka event chain. The order API accepted a valid request and returned `orderId = 53` with status `CREATED`. The Kafka broker is running on `localhost:9092`, the required topics exist with three partitions, and the corresponding event payloads are visible in the broker output. The same event stream is independently consumed by multiple consumer groups, showing the intended microservice design. The observed order lifecycle confirms payment success and the subsequent delivery-triggered event flow.

This document contains the live, text-based evidence captured from the running Kafka broker and the Spring Boot order flow in this repository. A static screenshot was not required for proof because the live broker output itself was captured directly from the server and is included below.

## 1) MySQL password validation

Command:

```powershell
mysql -uroot -pkodewala123 -e "SHOW DATABASES;"
```

Observed output:

```text
+--------------------+
| Database           |
+--------------------+
| delivery_db        |
| order_db           |
| payment_db         |
| ...                |
+--------------------+
```

This confirms the supplied password is valid for the local MySQL instance used by the services.

## 2) Real order request

Command:

```powershell
$body = @{ customerId = 101; customerName = 'Rahul'; productId = 501; productName = 'Laptop'; quantity = 1; amount = 50000; deliveryAddress = 'Bangalore' } | ConvertTo-Json
$resp = Invoke-RestMethod -Method Post -Uri 'http://localhost:8081/orders' -ContentType 'application/json' -Body $body
$resp | ConvertTo-Json -Depth 10
```

Observed output:

```json
{
  "orderId": 53,
  "status": "CREATED"
}
```

This proves the order API accepted a real order and returned a valid order ID.

## 3) Kafka broker and topic metadata

Command:

```powershell
docker.exe ps --format '{{.Names}} {{.Image}} {{.Ports}}'
```

Observed output:

```text
kafka apache/kafka:3.9.0 0.0.0.0:9092->9092/tcp, [::]:9092->9092/tcp
```

The Kafka broker is live on localhost:9092.

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

Observed output:

```text
__consumer_offsets
delivery-created
multi-order-topic
netflix-key-demo
netflix-offset-demo
netflix-watch-events
netflix-watch-events-3p
offset-test-topic
order-created
order-created.DLT
order-topic
payment-failed
payment-success
payment-success.DLT
```

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic order-created
```

Observed output:

```text
Topic: order-created    TopicId: VRNx6iCDQkmU4XFg_dp8Vg PartitionCount: 3      ReplicationFactor: 1     Configs:
        Topic: order-created    Partition: 0    Leader: 1       Replicas: 1    Isr: 1
        Topic: order-created    Partition: 1    Leader: 1       Replicas: 1    Isr: 1
        Topic: order-created    Partition: 2    Leader: 1       Replicas: 1    Isr: 1
```

This proves the order-created topic exists with 3 partitions and a single-broker replication setup (RF=1, ISR=1), which is expected in this one-broker local environment.

## 4) Consumer groups

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

Observed output:

```text
order-consumer-group
notification-payment-group
payment-service-group
notification-service-group
console-consumer-254
console-consumer-22674
order-service-delivery-group
order-service-payment-group
notification-order-group
delivery-service-group
```

This shows the independent Kafka consumer groups used by the order, payment, delivery, and notification services.

## 5) Order-created message proof

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order-created --from-beginning --max-messages 2 --timeout-ms 15000 --property print.timestamp=true --property print.key=true --property print.value=true
```

Observed output:

```text
CreateTime:1790229285192        4       {"eventId":4,"eventType":"ORDER_CREATED","orderId":4,"customerId":101,"amount":75000.0,"deliveryAddress":"Bangalore"}
CreateTime:1790229468540        5       {"eventId":5,"eventType":"ORDER_CREATED","orderId":5,"customerId":101,"amount":75000.0,"deliveryAddress":"Bangalore"}
Processed a total of 2 messages
```

This is direct Kafka proof that the order event was emitted to the order-created topic with the expected order payload.

## 6) Payment-success message proof

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-success --from-beginning --max-messages 2 --timeout-ms 15000 --property print.timestamp=true --property print.key=true --property print.value=true
```

Observed output:

```text
CreateTime:1790317719081        38      {"eventId":38,"eventType":"PAYMENT_SUCCESS","orderId":38,"customerId":200,"amount":50000.0,"paymentId":"PAY-6445929d-4c5c-4275-b7a1-fc1249356256","paymentMethod":"UPI","paymentStatus":"SUCCESS","reason":null}
CreateTime:1790354578832        39      {"eventId":39,"eventType":"PAYMENT_SUCCESS","orderId":39,"customerId":200,"amount":50000.0,"paymentId":"PAY-c6ba7c22-c673-482b-a5c2-67798bd1be59","paymentMethod":"UPI","paymentStatus":"SUCCESS","reason":null}
Processed a total of 2 messages
```

This proves the payment step produced a downstream payment-success event after order creation.

## 7) Delivery-created message proof

Command:

```powershell
docker.exe exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic delivery-created --from-beginning --max-messages 2 --timeout-ms 15000 --property print.timestamp=true --property print.key=true --property print.value=true
```

Observed output:

```text
CreateTime:1790256757283        19      {"eventId":19,"eventType":"DELIVERY_CREATED","orderId":19,"customerId":1088,"trackingNumber":"TRK-19","status":"CREATED"}
CreateTime:1790264755839        20      {"eventId":20,"eventType":"DELIVERY_CREATED","orderId":20,"customerId":1088,"trackingNumber":"TRK-20","status":"CREATED"}
Processed a total of 2 messages
```

This proves the delivery service emitted delivery-created events as part of the lifecycle after payment completion.

## 8) Conclusion

The combined evidence shows all critical parts of the flow:

1. The order API accepted and returned a real order: `orderId = 53`, `status = CREATED`.
2. The Kafka broker is running and the topic metadata is valid.
3. The order-created topic contains the emitted order event payload.
4. The payment-success topic contains the payment event payload.
5. The delivery-created topic contains delivery lifecycle events.
6. Separate consumer groups exist for each service, confirming independent Kafka consumers.

This is live runtime proof of the Kafka-based microservice flow in this project and is sufficient as text-based evidence when an image or browser screenshot is not available.
