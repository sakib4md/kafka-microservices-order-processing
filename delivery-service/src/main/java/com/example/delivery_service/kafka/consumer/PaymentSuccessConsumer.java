package com.example.delivery_service.kafka.consumer;

import com.example.delivery_service.dto.DeliveryCreatedEvent;
import com.example.delivery_service.dto.PaymentSuccessEvent;
import com.example.delivery_service.entity.Delivery;
import com.example.delivery_service.kafka.producer.DeliveryEventProducer;
import com.example.delivery_service.repository.DeliveryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentSuccessConsumer {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer deliveryEventProducer;

    public PaymentSuccessConsumer(
            DeliveryRepository deliveryRepository,
            DeliveryEventProducer deliveryEventProducer) {

        this.deliveryRepository = deliveryRepository;
        this.deliveryEventProducer = deliveryEventProducer;
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "delivery-service-group"
    )
        @Transactional
    public void consumePaymentSuccess(PaymentSuccessEvent paymentEvent) {

                if (deliveryRepository.existsByOrderId(paymentEvent.getOrderId())) {
                        System.out.println("Duplicate payment ignored for Order ID: "
                                        + paymentEvent.getOrderId());
                        return;
                }

        System.out.println("========== DELIVERY SERVICE ==========");
        System.out.println("Received PAYMENT_SUCCESS");
        System.out.println("Order ID: " + paymentEvent.getOrderId());

        Delivery delivery = new Delivery();

        delivery.setOrderId(paymentEvent.getOrderId());
        delivery.setCustomerId(paymentEvent.getCustomerId());
        delivery.setDeliveryAddress(paymentEvent.getDeliveryAddress());

        delivery.setTrackingNumber(
                "TRK-" + paymentEvent.getOrderId()
        );

        delivery.setStatus("CREATED");

        deliveryRepository.save(delivery);

        DeliveryCreatedEvent deliveryEvent = new DeliveryCreatedEvent(
                UUID.randomUUID().toString(),
                "DELIVERY_CREATED",
                delivery.getOrderId(),
                delivery.getCustomerId(),
                delivery.getDeliveryAddress(),
                delivery.getTrackingNumber(),
                delivery.getStatus(),
                Instant.now().toString()
        );
        deliveryEventProducer.publishDeliveryCreated(deliveryEvent);

        System.out.println("Delivery created successfully!");
        System.out.println("Tracking Number: " + delivery.getTrackingNumber());
        System.out.println("======================================");
    }
}