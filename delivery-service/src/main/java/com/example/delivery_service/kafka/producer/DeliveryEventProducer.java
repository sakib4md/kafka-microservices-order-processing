package com.example.delivery_service.kafka.producer;

import com.example.delivery_service.dto.DeliveryCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeliveryEventProducer {

    private static final String TOPIC = "delivery-created";
    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;

    public DeliveryEventProducer(
            KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishDeliveryCreated(DeliveryCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );
    }
}
