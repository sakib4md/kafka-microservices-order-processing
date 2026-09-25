package com.example.notification_service.kafka.consumer;

import com.example.notification_service.dto.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentNotificationConsumer {

    @KafkaListener(
            topics = "payment-success",
            groupId = "notification-payment-group"
    )
    public void consumePaymentSuccess(PaymentEvent event) {

        System.out.println("========== PAYMENT NOTIFICATION ==========");
        System.out.println("Received PAYMENT_SUCCESS");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Payment ID: " + event.getPaymentId());
        System.out.println("Amount: " + event.getAmount());
        System.out.println("Payment Method: " + event.getPaymentMethod());
        System.out.println("Sending Payment Success SMS...");
        System.out.println("Sending Payment Success Email...");
        System.out.println("==========================================");
    }

    @KafkaListener(
            topics = "payment-failed",
            groupId = "notification-payment-group"
    )
    public void consumePaymentFailed(PaymentEvent event) {

        System.out.println("========== PAYMENT NOTIFICATION ==========");
        System.out.println("Received PAYMENT_FAILED");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Payment ID: " + event.getPaymentId());
        System.out.println("Amount: " + event.getAmount());
        System.out.println("Reason: " + event.getReason());
        System.out.println("Sending Payment Failed SMS...");
        System.out.println("Sending Payment Failed Email...");
        System.out.println("==========================================");
    }
}