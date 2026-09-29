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

        System.out.println("Received " + event.getEventType());
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Tracking Number: " + event.getTrackingNumber());
        System.out.println("Delivery Address: " + event.getDeliveryAddress());
        System.out.println("Delivery Status: " + event.getDeliveryStatus());
        System.out.println("SMS SENT: " + notificationMessage(event));
        System.out.println("Email SENT: " + notificationMessage(event));

        System.out.println("==========================================");
    }

        private String notificationMessage(DeliveryCreatedEvent event) {
        return switch (event.getEventType()) {
            case "DELIVERY_CREATED" -> "Delivery created for order "
                + event.getOrderId() + ". Tracking: " + event.getTrackingNumber();
            case "DELIVERY_OUT_FOR_DELIVERY" -> "Order " + event.getOrderId()
                + " is out for delivery.";
            case "ORDER_DELIVERED" -> "Order " + event.getOrderId() + " was delivered.";
            case "DELIVERY_CANCELLED" -> "Delivery for order " + event.getOrderId()
                + " was cancelled.";
            default -> "Order " + event.getOrderId() + " delivery status: "
                + event.getDeliveryStatus() + ".";
        };
        }
}