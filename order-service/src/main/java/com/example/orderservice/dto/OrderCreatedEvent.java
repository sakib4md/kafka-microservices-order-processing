package com.example.orderservice.dto;

public class OrderCreatedEvent {

    private String eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private String deliveryAddress;
    private String eventTime;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            String eventId,
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