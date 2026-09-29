package com.example.delivery_service.controller;

import com.example.delivery_service.dto.UpdateDeliveryStatusRequest;
import com.example.delivery_service.entity.Delivery;
import com.example.delivery_service.service.DeliveryService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PatchMapping("/{orderId}/status")
    public Delivery updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateDeliveryStatusRequest request) {
        return deliveryService.updateStatus(orderId, request.getStatus());
    }
}