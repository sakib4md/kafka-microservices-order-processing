# Kafka Demo Evidence Guide

This guide describes how to capture real Kafka evidence for the project using live command output. The repository currently holds text-based and HTML proof rather than real `.png` screenshot files. Do not use sample or fabricated output as runtime evidence.

## Requirements and Replication Notes

The project's standard local topic setup creates three partitions with replication factor one. On a one-broker cluster, topic metadata therefore reports one replica and one in-sync replica (`Replicas: <broker-id>`, `Isr: <broker-id>`). This is expected, but it does not demonstrate broker redundancy.

To demonstrate multiple replicas and ISR, use a Kafka cluster with at least three running brokers and create the topics with replication factor three. All three broker IDs must be in the cluster. If topics already exist with replication factor one, creating them again with replication factor three will not change them; use Kafka partition reassignment or recreate them only in a disposable demo cluster.

ISR means **In-Sync Replicas**: replicas that are caught up enough to be eligible for leader election. For a healthy RF=3 partition, expect three replica IDs and the same three IDs in `Isr`. If a follower is stopped in a disposable test cluster, ISR should shrink until it catches up again. Never stop a broker in a shared or production cluster for this demonstration.

## Prepare the PowerShell Session

Set `KAFKA_HOME` to the extracted Kafka distribution folder. Run these commands from the repository root; replace the example path with the actual installation path.

```powershell
$env:KAFKA_HOME = "C:\kafka"
$KafkaBin = Join-Path $env:KAFKA_HOME "bin\windows"
$Bootstrap = "localhost:9092"

Test-NetConnection localhost -Port 9092
Test-Path (Join-Path $KafkaBin "kafka-topics.bat")
```

Start Kafka and MySQL, set `DB_PASSWORD` in the service-start shell, and start all four services. Import `postman/Kafka-Order-Processing.postman_collection.json` into Postman. The app ports are Order `8081`, Payment `8082`, Delivery `8083`, and Notification `8084`.

Create topics before starting the services. Set `$ReplicationFactor = 1` for the repository's default single-broker setup, or `3` only when three brokers are live:

```powershell
$ReplicationFactor = 1
$Topics = @("order-created", "payment-success", "payment-failed", "delivery-created", "payment-success.DLT")
foreach ($Topic in $Topics) {
  & (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --create --if-not-exists --topic $Topic --partitions 3 --replication-factor $ReplicationFactor
}
```

For a replicated run, set `$ReplicationFactor = 3` before creating the topics. Kafka will reject RF=3 if fewer than three brokers are available.

## Exact Commands for Command Prompt and PowerShell

Use either Command Prompt or PowerShell. The repository examples below are intentionally written in the native syntax for both shells.

### Command Prompt (cmd.exe)

```cmd
set KAFKA_HOME=C:\kafka
set BOOTSTRAP=localhost:9092

"%KAFKA_HOME%\bin\windows\kafka-topics.bat" --bootstrap-server %BOOTSTRAP% --list
"%KAFKA_HOME%\bin\windows\kafka-topics.bat" --bootstrap-server %BOOTSTRAP% --describe --topic order-created
"%KAFKA_HOME%\bin\windows\kafka-consumer-groups.bat" --bootstrap-server %BOOTSTRAP% --list
"%KAFKA_HOME%\bin\windows\kafka-consumer-groups.bat" --bootstrap-server %BOOTSTRAP% --describe --group payment-service-group
"%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat" --bootstrap-server %BOOTSTRAP% --topic order-created --from-beginning --max-messages 3 --property print.timestamp=true --property print.key=true --property print.value=true
"%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat" --bootstrap-server %BOOTSTRAP% --topic payment-success --from-beginning --max-messages 3 --property print.timestamp=true --property print.key=true --property print.value=true
"%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat" --bootstrap-server %BOOTSTRAP% --topic payment-success.DLT --from-beginning --property print.timestamp=true --property print.key=true --property print.value=true
```

### PowerShell

```powershell
$env:KAFKA_HOME = "C:\kafka"
$KafkaBin = Join-Path $env:KAFKA_HOME "bin\windows"
$Bootstrap = "localhost:9092"

& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --list
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe --topic order-created
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --list
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group payment-service-group
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic order-created --from-beginning --max-messages 3 --property print.timestamp=true --property print.key=true --property print.value=true
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic payment-success --from-beginning --max-messages 3 --property print.timestamp=true --property print.key=true --property print.value=true
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic payment-success.DLT --from-beginning --property print.timestamp=true --property print.key=true --property print.value=true
```

## Text-Based Proof Workflow

This repository does not currently contain real `.png` screenshot files. The valid runtime evidence is the live Kafka output captured directly from the broker and saved as text in [kafka-live-proof.md](kafka-live-proof.md) and as HTML in [../screenshots/live-kafka-proof.html](../screenshots/live-kafka-proof.html).

If you want to capture actual images later on a machine with a GUI, use the same commands below and save the output with Snipping Tool. Until then, the repo should be treated as a text-evidence project, not a screenshot-evidence project.

## Scenario Commands

### 1. Topic Names and Partition/Replica/ISR Metadata

Run the following and capture the output. The all-topics description includes each topic's partition count, leader, replicas, and ISR.

```powershell
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --list
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe

# Optional health views. Empty output means there are no matching partitions.
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe --under-replicated-partitions
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe --unavailable-partitions
```

For RF=3, each partition should list three broker IDs under both `Replicas` and `Isr` while healthy. If `Isr` has fewer IDs than `Replicas`, the partition has an out-of-sync replica. With the project's RF=1 setup, `Replicas` and `Isr` each contain just one broker ID.

### 2. Successful and Failed Message Flow

Start a console consumer in a separate terminal before sending orders. Use the topic matching the event being demonstrated:

```powershell
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic order-created --from-beginning --property print.timestamp=true --property print.key=true --property print.partition=true --property print.headers=true
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic payment-success --from-beginning --property print.timestamp=true --property print.key=true --property print.partition=true --property print.headers=true
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic payment-failed --from-beginning --property print.timestamp=true --property print.key=true --property print.partition=true --property print.headers=true
```

In Postman, send the successful order (amount `50000`) and failed order (amount `60000`). Capture the console consumer record and the corresponding service log. Use `Ctrl+C` to stop each console consumer after evidence is captured. Record the output as plain text evidence in the same way as [kafka-live-proof.md](kafka-live-proof.md).

### 3. Consumer Groups, Members, Offsets, and Lag

Groups appear after a service has joined and consumed records. Capture the list, then capture each important group's state:

```powershell
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --list
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group payment-service-group
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group order-service-payment-group
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group delivery-service-group
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group notification-payment-group
```

The describe output includes topic, partition, current offset, log-end offset, lag, consumer ID, host, and client ID. Consumer-group offsets belong to the group, not to the topic globally.

### 4. Scale Payment Consumers

Leave the normal Payment Service running. In separate terminals, start two more instances with the same configured group and different HTTP ports:

```powershell
Push-Location .\payment-service; .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8085"
Push-Location .\payment-service; .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8086"
```

Then inspect group membership and assignments:

```powershell
& (Join-Path $KafkaBin "kafka-consumer-groups.bat") --bootstrap-server $Bootstrap --describe --group payment-service-group --members --verbose
```

With three topic partitions, at most three members of this group can actively own partitions. Capture this output as text and note the group state in the proof log; there is no real screenshot file in the repository for this item.

### 5. Stop and Restart Retry

Stop all Payment Service instances, submit an order using Postman, and restart one instance. Inspect `order-created` and `payment-service-group` offsets; the order should be processed after the group resumes. Record the stopped-service and resumed-processing states in a text log rather than expecting a real screenshot file.

### 6. Duplicate Event and Idempotency

Order Service publishes each order event twice with the same `eventId`. Submit one normal order and capture both source records using the `order-created` console consumer. Then inspect the database:

```sql
USE payment_db;
SELECT event_id, COUNT(*) AS payment_count
FROM payments
GROUP BY event_id
ORDER BY MAX(id) DESC;

SELECT event_id, event_type, processed_at
FROM processed_events
ORDER BY id DESC;
```

For a specific event ID, the payment row count should be one and the duplicate should be logged as ignored. Capture the Kafka output and SQL result together in text format; do not fabricate a `.png` file that does not exist.

### 7. Retry and Dead-Letter Topic

Submit the retry-test order with amount `55555`. Payment Service should make an initial attempt plus two retries at two-second intervals. After recovery, read the DLT:

```powershell
& (Join-Path $KafkaBin "kafka-console-consumer.bat") --bootstrap-server $Bootstrap --topic payment-success.DLT --from-beginning --property print.timestamp=true --property print.key=true --property print.partition=true --property print.headers=true
```

Capture Payment Service retry logs and the DLT record as plain text. The assignment requests the DLT name `payment-success.DLT`; the failed input record originated in `order-created`.

### 8. Delivery Lifecycle Notifications

Run the successful order request from the Postman collection, then run its delivery-status requests in order: `IN_TRANSIT`, `OUT_FOR_DELIVERY`, and `DELIVERED`. For cancellation evidence, create a separate successful order and set its delivery to `CANCELLED` before terminal delivery status. Capture `delivery-created` records and Notification Service logs as text output; no screenshot file is required or present.

### 9. Optional ISR Shrink and Recovery (Multi-Broker Only)

Only perform this on a disposable cluster with RF=3 topics. Capture healthy metadata first. Stop one non-leader broker using that cluster's normal broker-management procedure, then run:

```powershell
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe --under-replicated-partitions
& (Join-Path $KafkaBin "kafka-topics.bat") --bootstrap-server $Bootstrap --describe --topic payment-success
```

The stopped replica should be absent from ISR until it catches up after restart. Capture the ISR change and restored ISR in plain text output. Do not perform this test on a shared broker.