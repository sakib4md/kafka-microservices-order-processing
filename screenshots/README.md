# Kafka Screenshot Evidence

Capture instructions and screenshot filenames are in [the Kafka evidence guide](../docs/kafka-evidence-and-screenshots.md).

## Capture Status

No real PNG screenshots were created in this environment. The broker and Kafka commands were validated live, but the actual Windows Snipping Tool screenshot files were not captured here. The project therefore includes text-based and HTML proof records instead of fabricated `.png` evidence.

The current broker is a single Kafka 3.9.0 node. Its topic setup supports three partitions but has replication factor one, so it can only show ISR=1. Use at least three brokers and RF=3 topics to capture multi-replica ISR evidence.

The required `payment-success.DLT` topic has been created on the local broker with three partitions and RF=1. This is a non-destructive topic creation; existing topics were left unchanged.

Runtime evidence is available in [../docs/kafka-live-proof.md](../docs/kafka-live-proof.md) and [live-kafka-proof.html](live-kafka-proof.html). Save genuine screenshot files using the filenames in the guide when you run the demo on a machine with a GUI and Snipping Tool enabled. Do not use placeholders or fabricated screenshots as evidence.