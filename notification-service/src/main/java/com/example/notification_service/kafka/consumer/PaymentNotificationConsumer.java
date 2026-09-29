package com.example.notification_service.kafka.consumer;

import com.example.notification_service.dto.PaymentEvent;
import com.example.notification_service.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentNotificationConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-order-group"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        System.out.println("SMS SENT: Order " + event.getOrderId()
                + " was created for customer " + event.getCustomerId() + ".");
    }

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
        String message = "Your payment of " + formatAmount(event.getAmount())
            + " was successful.";
        System.out.println("SMS SENT: Customer: " + event.getCustomerId()
            + ", Order: " + event.getOrderId() + ". " + message);
        System.out.println("EMAIL SENT: " + message);
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
        String message = "Your payment of " + formatAmount(event.getAmount())
                + " failed. Reason: " + event.getReason() + ".";
        System.out.println("SMS SENT: Customer: " + event.getCustomerId()
                + ", Order: " + event.getOrderId() + ". " + message);
        System.out.println("EMAIL SENT: " + message);
        System.out.println("==========================================");
    }

    private String formatAmount(Double amount) {
        return amount == null ? "an unknown amount" : String.format("Rs.%,.0f", amount);
    }
}