package com.example.delivery_service.service;

import com.example.delivery_service.dto.DeliveryCreatedEvent;
import com.example.delivery_service.entity.Delivery;
import com.example.delivery_service.entity.DeliveryStatus;
import com.example.delivery_service.kafka.producer.DeliveryEventProducer;
import com.example.delivery_service.repository.DeliveryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer deliveryEventProducer;

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            DeliveryEventProducer deliveryEventProducer) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryEventProducer = deliveryEventProducer;
    }

    @Transactional
    public Delivery updateStatus(Long orderId, String requestedStatus) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Delivery not found for order " + orderId));

        DeliveryStatus nextStatus;
        try {
            nextStatus = DeliveryStatus.valueOf(requestedStatus);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unsupported delivery status: " + requestedStatus);
        }

        DeliveryStatus currentStatus = DeliveryStatus.valueOf(delivery.getStatus());
        if (!currentStatus.canTransitionTo(nextStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot change delivery status from " + currentStatus + " to " + nextStatus);
        }

        delivery.setStatus(nextStatus.name());
        Delivery savedDelivery = deliveryRepository.save(delivery);
        String eventType = nextStatus == DeliveryStatus.DELIVERED
                ? "ORDER_DELIVERED"
                : "DELIVERY_" + nextStatus.name();

        deliveryEventProducer.publishDeliveryCreated(new DeliveryCreatedEvent(
                UUID.randomUUID().toString(),
                eventType,
                savedDelivery.getOrderId(),
                savedDelivery.getCustomerId(),
                savedDelivery.getDeliveryAddress(),
                savedDelivery.getTrackingNumber(),
                savedDelivery.getStatus(),
                Instant.now().toString()
        ));
        return savedDelivery;
    }
}