package com.example.orderservice.dto;

public class OrderCreatedEvent {

    private Long eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private String deliveryAddress;
    private String eventTime;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            Long eventId,
            String eventType,
            Long orderId,
            Long customerId,
            Double amount,
            String deliveryAddress,
            String eventTime) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.deliveryAddress = deliveryAddress;
        this.eventTime = eventTime;
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

    public Double getAmount() {
        return amount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getEventTime() {
        return eventTime;
    }


}