package com.example.orderservice.kafka.consumer;

import com.example.orderservice.dto.DeliveryEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeliveryEventConsumer {

    private final OrderRepository orderRepository;

    public DeliveryEventConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "delivery-created",
            groupId = "order-service-delivery-group"
    )
    public void consumeDeliveryEvent(DeliveryEvent event) {
        String status = switch (event.getEventType()) {
            case "DELIVERY_IN_TRANSIT" -> "IN_TRANSIT";
            case "DELIVERY_OUT_FOR_DELIVERY" -> "OUT_FOR_DELIVERY";
            case "ORDER_DELIVERED" -> "DELIVERED";
            case "DELIVERY_CANCELLED" -> "CANCELLED";
            default -> null;
        };
        if (status == null) {
            return;
        }

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}