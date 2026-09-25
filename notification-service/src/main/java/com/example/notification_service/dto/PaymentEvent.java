package com.example.notification_service.dto;

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

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}