package com.example.delivery_service.repository;

import com.example.delivery_service.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
	boolean existsByOrderId(Long orderId);

	java.util.Optional<Delivery> findByOrderId(Long orderId);
}