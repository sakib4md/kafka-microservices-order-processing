package com.example.delivery_service.entity;

public enum DeliveryStatus {
    CREATED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(DeliveryStatus next) {
        return switch (this) {
            case CREATED -> next == IN_TRANSIT || next == CANCELLED;
            case IN_TRANSIT -> next == OUT_FOR_DELIVERY || next == CANCELLED;
            case OUT_FOR_DELIVERY -> next == DELIVERED || next == CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}