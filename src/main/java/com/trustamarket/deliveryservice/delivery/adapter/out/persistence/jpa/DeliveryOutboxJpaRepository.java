package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryOutboxJpaRepository extends JpaRepository<DeliveryOutboxJpaEntity, UUID> {

    List<DeliveryOutboxJpaEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
