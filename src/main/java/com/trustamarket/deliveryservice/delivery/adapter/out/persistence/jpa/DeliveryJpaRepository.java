package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryJpaEntity, UUID> {

    List<DeliveryJpaEntity> findAllByStatus(DeliveryStatus status);

    Optional<DeliveryJpaEntity> findByOrderId(UUID orderId);
}
