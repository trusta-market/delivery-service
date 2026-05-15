package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryJpaEntity, UUID> {
}
