package com.example.delivery_service.kafka.consumer;

import com.example.delivery_service.dto.DeliveryCreatedEvent;
import com.example.delivery_service.dto.PaymentSuccessEvent;
import com.example.delivery_service.entity.Delivery;
import com.example.delivery_service.kafka.producer.DeliveryEventProducer;
import com.example.delivery_service.repository.DeliveryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
    public void consumePaymentSuccess(PaymentSuccessEvent paymentEvent) {

        System.out.println("========== DELIVERY SERVICE ==========");
        System.out.println("Received PAYMENT_SUCCESS");
        System.out.println("Order ID: " + paymentEvent.getOrderId());

        Delivery delivery = new Delivery();

        delivery.setOrderId(paymentEvent.getOrderId());
        delivery.setCustomerId(paymentEvent.getCustomerId());

        delivery.setTrackingNumber(
                "TRK-" + paymentEvent.getOrderId()
        );

        delivery.setStatus("CREATED");

        deliveryRepository.save(delivery);

        DeliveryCreatedEvent deliveryEvent = new DeliveryCreatedEvent(
                paymentEvent.getEventId(),
                "DELIVERY_CREATED",
                delivery.getOrderId(),
                delivery.getCustomerId(),
                delivery.getTrackingNumber(),
                delivery.getStatus()
        );
        deliveryEventProducer.publishDeliveryCreated(deliveryEvent);

        System.out.println("Delivery created successfully!");
        System.out.println("Tracking Number: " + delivery.getTrackingNumber());
        System.out.println("======================================");
    }
}