package com.example.payment_service.dto;

public class PaymentEvent {

    private Long eventId;
    private String eventType;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private String paymentId;
    private String paymentMethod;
    private String paymentStatus;
    private String reason;

    public PaymentEvent() {
    }

    public PaymentEvent(
            Long eventId,
            String eventType,
            Long orderId,
            Long customerId,
            Double amount,
            String paymentId,
            String paymentMethod,
            String paymentStatus,
            String reason) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.reason = reason;
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

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getReason() {
        return reason;
    }
}