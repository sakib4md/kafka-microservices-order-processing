package com.example.notification_service.kafka.consumer;

import com.example.notification_service.dto.DeliveryCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCreatedConsumer {

    @KafkaListener(
            topics = "delivery-created",
            groupId = "notification-service-group"
    )
    public void consumeDeliveryCreated(DeliveryCreatedEvent event) {

        System.out.println("========== NOTIFICATION SERVICE ==========");

        System.out.println("Received DELIVERY_CREATED");

        System.out.println("Order ID: " + event.getOrderId());

        System.out.println("Customer ID: " + event.getCustomerId());

        System.out.println("Tracking Number: "
                + event.getTrackingNumber());

        System.out.println("Status: " + event.getStatus());

        System.out.println("Sending SMS...");

        System.out.println("Sending Email...");

        System.out.println("==========================================");
    }
}