# Kafka Topics and Consumer Groups

For screenshot filenames, scenario-by-scenario evidence steps, replication factor, and ISR details, see [Kafka Demo Evidence and Screenshot Guide](kafka-evidence-and-screenshots.md).

## Topic and Group Map

Main topics use three partitions and replication factor one in the local setup. Producers use `orderId` as the Kafka record key, so records for the same order are assigned to the same partition and remain ordered within that partition.

| Topic | Producer | Consumer group | Consumer / purpose |
| --- | --- | --- | --- |
| `order-created` | Order Service | `payment-service-group` | Payment Service processes each order once. |
| `order-created` | Order Service | `notification-order-group` | Notification Service independently observes new orders. |
| `payment-success` | Payment Service | `order-service-payment-group` | Order Service marks the order paid. |
| `payment-success` | Payment Service | `delivery-service-group` | Delivery Service creates one delivery per order. |
| `payment-success` | Payment Service | `notification-payment-group` | Notification Service sends payment success messages. |
| `payment-failed` | Payment Service | `order-service-payment-group` | Order Service marks payment failure. |
| `payment-failed` | Payment Service | `notification-payment-group` | Notification Service sends payment failure messages. |
| `delivery-created` | Delivery Service | `order-service-delivery-group` | Order Service receives delivery status changes. |
| `delivery-created` | Delivery Service | `notification-service-group` | Notification Service sends delivery updates. |
| `payment-success.DLT` | Payment Service error handler | None; inspected manually | Holds exhausted `order-created` records after retries. |

Consumer groups maintain independent offsets. The same `payment-success` record is read independently by Order, Delivery, and Notification services. Within a group, each partition is assigned to at most one active consumer at a time; additional service instances in the same group share partition work.

## Event-to-Topic Map

| Event type | Topic | Important fields |
| --- | --- | --- |
| `ORDER_CREATED` | `order-created` | `eventId`, `eventTime`, `orderId`, `customerId`, `amount`, `deliveryAddress` |
| `PAYMENT_SUCCESS` | `payment-success` | `eventId`, `eventTime`, `orderId`, `paymentId`, `paymentMethod`, `paymentStatus`, `deliveryAddress` |
| `PAYMENT_FAILED` | `payment-failed` | `eventId`, `eventTime`, `orderId`, `paymentId`, `paymentStatus`, `reason` |
| `DELIVERY_CREATED` | `delivery-created` | `eventId`, `eventTime`, `orderId`, `trackingNumber`, `deliveryAddress`, `deliveryStatus` |
| `DELIVERY_IN_TRANSIT` | `delivery-created` | `eventId`, `eventTime`, `orderId`, `trackingNumber`, `deliveryStatus` |
| `DELIVERY_OUT_FOR_DELIVERY` | `delivery-created` | `eventId`, `eventTime`, `orderId`, `trackingNumber`, `deliveryStatus` |
| `ORDER_DELIVERED` | `delivery-created` | `eventId`, `eventTime`, `orderId`, `trackingNumber`, `deliveryStatus` |
| `DELIVERY_CANCELLED` | `delivery-created` | `eventId`, `eventTime`, `orderId`, `trackingNumber`, `deliveryStatus` |

Event IDs are strings and support idempotency independently of the Kafka record key. Each producer uses `orderId` for the record key.

## Example Kafka Records and Responses

The following examples are representative of records produced by the services in this demo. The same `orderId` is used as the Kafka key, so records for one order go to the same partition and remain ordered within that partition.

### `order-created`

Example record written by the Order Service:

```json
{
  "eventId": "evt-1001",
  "eventType": "ORDER_CREATED",
  "orderId": 101,
  "customerId": 2001,
  "amount": 50000.0,
  "deliveryAddress": "Bangalore",
  "createdAt": "2026-09-29T10:12:45.123Z"
}
```

Kafka key:

```text
101
```

Consumer behavior:

- `payment-service-group` reads the event and processes payment.
- `notification-order-group` reads the same event independently for order notifications.
- If the same event is emitted twice, the payment service checks `eventId` and ignores duplicate processing.

### `payment-success`

Example outcome after a valid payment:

```json
{
  "eventId": "evt-1001-payment-success",
  "eventType": "PAYMENT_SUCCESS",
  "orderId": 101,
  "customerId": 2001,
  "amount": 50000.0,
  "paymentId": "PAY-5b7e3d8d-2b7f-4fd7-b5d1-ea1d89a63d02",
  "paymentMethod": "UPI",
  "paymentStatus": "SUCCESS",
  "reason": null
}
```

Kafka key:

```text
101
```

This topic is consumed by:

- `order-service-payment-group` to update the order to `PAID`
- `delivery-service-group` to create a delivery
- `notification-payment-group` to send payment notifications

### `payment-failed`

Example failed payment:

```json
{
  "eventId": "evt-1002-payment-failed",
  "eventType": "PAYMENT_FAILED",
  "orderId": 102,
  "customerId": 2002,
  "amount": 60000.0,
  "paymentId": "PAY-44e98a02-8246-4fd2-a48b-e2459af5ecad",
  "paymentMethod": "CARD",
  "paymentStatus": "FAILED",
  "reason": "INSUFFICIENT_FUNDS"
}
```

Kafka key:

```text
102
```

This proves failure handling in the Kafka chain and allows the order service to mark an order as `PAYMENT_FAILED` while notification consumers deliver the failure message.

### `delivery-created`

Example delivery creation record:

```json
{
  "eventId": "evt-1001-delivery-created",
  "eventType": "DELIVERY_CREATED",
  "orderId": 101,
  "customerId": 2001,
  "trackingNumber": "TRK-101",
  "status": "CREATED",
  "deliveryAddress": "Bangalore"
}
```

Kafka key:

```text
101
```

This stream is later updated for lifecycle transitions such as `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, and `CANCELLED` using the same unique order key.

### `payment-success.DLT`

After retries are exhausted, a bad or unprocessable message is moved to the dead-letter topic:

```json
{
  "eventId": "evt-1003",
  "eventType": "ORDER_CREATED",
  "orderId": 103,
  "customerId": 2003,
  "amount": 55555.0,
  "deliveryAddress": "Hyderabad",
  "error": "Payment processing failed after retries. Message moved to DLT."
}
```

This demonstrates the failure and retry path: the service retries, then moves the exhausted message to `payment-success.DLT` so it can be inspected or replayed manually.

## Multiple Consumer and Partition Behavior

This project intentionally uses multiple partitions and independent consumer groups.

### Example: same event in multiple groups

The same `PAYMENT_SUCCESS` event can be read by multiple consumer groups at the same time:

```text
payment-service-group      -> payment event consumed by Payment Service
order-service-payment-group -> order service marks the order as PAID
delivery-service-group     -> delivery service creates the delivery
notification-service-group -> notification service sends delivery updates
```

Each group maintains its own offset. This means the same Kafka record is not shared as a single queue; each group is a logical copy of the stream.

### Example: multiple partitions and ordering

Because `orderId` is the record key, all records for a given order go to the same partition:

```text
key=101 -> partition 1
key=102 -> partition 2
key=101 -> partition 1 again
```

This preserves ordering for one order while allowing different orders to be processed in parallel across partitions.

### Example: consumer scaling

For the payment topic, multiple Payment Service instances can run in the same group:

```text
payment-service-group
  - payment-instance-1
  - payment-instance-2
  - payment-instance-3
```

Kafka assigns partitions across the active consumers in the group. With three partitions, up to three consumers in the same group can process records concurrently. One order stays ordered within its partition, even when consumers scale horizontally.

### Example: failure and retry path

```text
Initial attempt: payment fails due to a controlled test exception
First retry: 2-second delay
Second retry: 2-second delay
Third retry: exhausted and moved to payment-success.DLT
```

The DLT shows the failed record after the configured retry limit is hit.

## Kafka Concepts in This Project

- **Topic:** named stream of records, such as `order-created`.
- **Partition:** ordered segment of a topic. Three partitions allow different orders to be processed concurrently.
- **Offset:** a consumer group's position in one partition; groups advance offsets independently.
- **Producer:** service that publishes a record. Order, Payment, and Delivery Services each produce events.
- **Consumer:** service listener that reads records from a topic.
- **Consumer group:** named set of consumers sharing partition assignments. Different groups each get their own logical copy of the stream.

## Create Topics

Run these commands from the Kafka installation directory in PowerShell:

```powershell
$topics = @("order-created", "payment-success", "payment-failed", "delivery-created", "payment-success.DLT")
foreach ($topic in $topics) {
  .\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --create --if-not-exists --topic $topic --partitions 3 --replication-factor 1
}
```

## Inspect Topics, Records, and Groups

```powershell
# List topics
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --list

# Show partition count and leaders for one topic
.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 --describe --topic payment-success

# Read records and display key and partition
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic payment-success --from-beginning --property print.key=true --property print.partition=true

# List groups and inspect a group's partition assignments and offsets
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --group delivery-service-group

# Inspect exhausted retry records
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic payment-success.DLT --from-beginning --property print.key=true --property print.headers=true
```

Groups appear after consumers connect. `payment-success.DLT` receives the `order-created` input record after the initial attempt and two retries; the configured DLT name is intentionally the one required by this project exercise.

## Scale and Failure Exercises

### Scale Payment Service

Keep the normal instance running, then start these commands in separate terminals from the repository root. They share `payment-service-group` but use distinct HTTP ports:

```powershell
Push-Location .\payment-service; .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"
Push-Location .\payment-service; .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8086"
```

Kafka distributes the group's partitions across instances. With three partitions, no more than three instances can actively consume partitions in this group.

### Stop and Restart

Stop all Payment Service instances, submit an order, and restart one instance. The pending `order-created` record should be consumed after the service rejoins `payment-service-group`.

### Duplicate Event

Order Service intentionally publishes the same order event twice with one `eventId`. Verify the payment database has one payment row for that event and that Payment Service logs the duplicate as ignored. Delivery creation is also protected by a unique `orderId`.

### Retry and DLT

Submit an order with amount `55555`. Payment Service throws on that test value, retries after two seconds twice, then writes the exhausted record to `payment-success.DLT`. Inspect it with:

```powershell
.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic payment-success.DLT --from-beginning --property print.key=true --property print.headers=true
```

### Demo Evidence

Capture live screenshots showing the topic list and partition counts, keyed records, consumer-group assignment and offsets, successful and failed notification output, retry attempts, and the DLT record. These screenshots require running Kafka and the services; they are not generated from source files.