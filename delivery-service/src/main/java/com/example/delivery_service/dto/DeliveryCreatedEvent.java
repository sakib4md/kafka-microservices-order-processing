package com.example.delivery_service.dto;

public class DeliveryCreatedEvent {

    private String eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private String deliveryAddress;
    private String trackingNumber;
    private String deliveryStatus;
    private String eventTime;

    public DeliveryCreatedEvent() {
    }

    public DeliveryCreatedEvent(
            String eventId,
            String eventType,
            Long orderId,
            Long customerId,
            String deliveryAddress,
            String trackingNumber,
            String deliveryStatus,
            String eventTime) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.trackingNumber = trackingNumber;
        this.deliveryStatus = deliveryStatus;
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getEventTime() {
        return eventTime;
    }
}