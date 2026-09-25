package com.example.payment_service.kafka.consumer;

import com.example.payment_service.dto.OrderCreatedEvent;
import com.example.payment_service.dto.PaymentEvent;
import com.example.payment_service.entity.Payment;
import com.example.payment_service.kafka.producer.PaymentEventProducer;
import com.example.payment_service.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderCreatedConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    public OrderCreatedConsumer(
            PaymentRepository paymentRepository,
            PaymentEventProducer paymentEventProducer) {

        this.paymentRepository = paymentRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service-group"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        if (paymentRepository.existsByEventId(event.getEventId())) {
            System.out.println("Duplicate event ignored. Event ID: " + event.getEventId());
            return;
        }
        System.out.println("Processing payment for Order ID: "
                + event.getOrderId());

        // Payment simulation
        if (event.getAmount() == 55555.0) {
            System.out.println("TEST FAILURE - triggering Kafka retry");
            throw new RuntimeException("TEST RETRY");
        }
        boolean paymentSuccess = event.getAmount() < 60000;

        String paymentId = "PAY-" + UUID.randomUUID();

        String paymentMethod = "UPI";

        String paymentStatus =
                paymentSuccess ? "SUCCESS" : "FAILED";

        String reason =
                paymentSuccess ? null : "INSUFFICIENT_FUNDS";

        // Create Payment DB record
        Payment payment = new Payment();

        payment.setPaymentId(paymentId);
        payment.setOrderId(event.getOrderId());
        payment.setCustomerId(event.getCustomerId());
        payment.setAmount(event.getAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(paymentStatus);
        payment.setReason(reason);
        payment.setEventId(event.getEventId());

        paymentRepository.save(payment);

        System.out.println("Payment saved to database");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Payment Status: " + paymentStatus);

        // Create Kafka event
        PaymentEvent paymentEvent = new PaymentEvent(
                event.getEventId(),
                paymentSuccess
                        ? "PAYMENT_SUCCESS"
                        : "PAYMENT_FAILED",
                event.getOrderId(),
                event.getCustomerId(),
                event.getAmount(),
                paymentId,
                paymentMethod,
                paymentStatus,
                reason
        );

        if (paymentSuccess) {

            System.out.println(
                    "PAYMENT SUCCESS for Order ID: "
                            + event.getOrderId()
            );

            paymentEventProducer.publishSuccess(paymentEvent);

        } else {

            System.out.println(
                    "PAYMENT FAILED for Order ID: "
                            + event.getOrderId()
            );

            paymentEventProducer.publishFailure(paymentEvent);
        }
    }
}