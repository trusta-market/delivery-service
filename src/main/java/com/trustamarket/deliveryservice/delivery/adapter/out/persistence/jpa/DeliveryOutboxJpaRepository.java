package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeliveryOutboxJpaRepository extends JpaRepository<DeliveryOutboxJpaEntity, UUID> {

    @Query(value = """
            SELECT * FROM p_delivery.p_delivery_outbox
            WHERE status = 'PENDING'
            ORDER BY created_at
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<DeliveryOutboxJpaEntity> findPendingForUpdateSkipLocked(@Param("limit") int limit);

    long countByStatus(OutboxStatus status);
}
