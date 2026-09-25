package com.example.payment_service.kafka.producer;

import com.example.payment_service.dto.PaymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentEventProducer(
            KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSuccess(PaymentEvent event) {
        kafkaTemplate.send(
                "payment-success",
                event.getOrderId().toString(),
                event
        );
    }

    public void publishFailure(PaymentEvent event) {
        kafkaTemplate.send(
                "payment-failed",
                event.getOrderId().toString(),
                event
        );
    }
}