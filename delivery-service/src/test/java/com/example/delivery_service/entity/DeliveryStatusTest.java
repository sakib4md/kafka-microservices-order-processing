package com.example.delivery_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryStatusTest {

    @Test
    void allowsForwardDeliveryTransitions() {
        assertTrue(DeliveryStatus.CREATED.canTransitionTo(DeliveryStatus.IN_TRANSIT));
        assertTrue(DeliveryStatus.IN_TRANSIT.canTransitionTo(DeliveryStatus.OUT_FOR_DELIVERY));
        assertTrue(DeliveryStatus.OUT_FOR_DELIVERY.canTransitionTo(DeliveryStatus.DELIVERED));
        assertTrue(DeliveryStatus.CREATED.canTransitionTo(DeliveryStatus.CANCELLED));
    }

    @Test
    void preventsTerminalAndBackwardTransitions() {
        assertFalse(DeliveryStatus.DELIVERED.canTransitionTo(DeliveryStatus.IN_TRANSIT));
        assertFalse(DeliveryStatus.CANCELLED.canTransitionTo(DeliveryStatus.DELIVERED));
        assertFalse(DeliveryStatus.OUT_FOR_DELIVERY.canTransitionTo(DeliveryStatus.CREATED));
    }
}