package com.example.orderservice.service;

import java.util.UUID;
import com.example.orderservice.dto.OrderCreatedEvent;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.kafka.producer.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer) {

        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    public OrderResponse createOrder(Order order) {

        // 1. Set order status
        order.setStatus("CREATED");

        // 2. Save order in database
        Order savedOrder = orderRepository.save(order);

        // 3. Create Kafka event
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "ORDER_CREATED",
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getAmount(),
                savedOrder.getDeliveryAddress(),
                Instant.now().toString()
        );

        // 4. Publish event to Kafka
        orderEventProducer.publishOrderCreated(event);
        orderEventProducer.publishOrderCreated(event); // TEST DUPLICATE

        // 5. Return clean response
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus()
        );
    }
}