package com.example.orderservice.kafka.consumer;

import com.example.orderservice.dto.PaymentEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    public PaymentEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "order-service-payment-group"
    )
    public void consumePaymentSuccess(PaymentEvent event) {

        System.out.println("========== ORDER STATUS UPDATE ==========");
        System.out.println("Received PAYMENT_SUCCESS");
        System.out.println("Order ID: " + event.getOrderId());

        updateOrderStatus(event.getOrderId(), "PAID");

        System.out.println("Order status updated to PAID");
        System.out.println("==========================================");
    }

    @KafkaListener(
            topics = "payment-failed",
            groupId = "order-service-payment-group"
    )
    public void consumePaymentFailed(PaymentEvent event) {

        System.out.println("========== ORDER STATUS UPDATE ==========");
        System.out.println("Received PAYMENT_FAILED");
        System.out.println("Order ID: " + event.getOrderId());

        updateOrderStatus(event.getOrderId(), "PAYMENT_FAILED");

        System.out.println("Order status updated to PAYMENT_FAILED");
        System.out.println("==========================================");
    }

    private void updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: " + orderId
                        ));

        order.setStatus(status);

        orderRepository.save(order);
    }
}