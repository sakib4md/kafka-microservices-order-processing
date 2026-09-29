package com.example.orderservice.dto;

public class DeliveryEvent {

    private String eventId;
    private String eventType;
    private Long orderId;
    private String deliveryStatus;

    public DeliveryEvent() {
    }

    public String getEventType() {
        return eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}