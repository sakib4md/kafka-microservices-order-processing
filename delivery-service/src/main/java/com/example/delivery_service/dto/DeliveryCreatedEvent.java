package com.example.delivery_service.dto;

public class DeliveryCreatedEvent {

    private Long eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private String trackingNumber;
    private String status;

    public DeliveryCreatedEvent() {
    }

    public DeliveryCreatedEvent(
            Long eventId,
            String eventType,
            Long orderId,
            Long customerId,
            String trackingNumber,
            String status) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public Long getEventId() {
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

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getStatus() {
        return status;
    }
}