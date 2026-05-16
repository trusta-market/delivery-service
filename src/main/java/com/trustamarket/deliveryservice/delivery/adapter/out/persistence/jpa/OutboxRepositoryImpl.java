package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.application.port.out.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final DeliveryOutboxJpaRepository jpaRepository;

    @Override
    public void save(String topic, String messageKey, String payload) {
        jpaRepository.save(DeliveryOutboxJpaEntity.of(topic, messageKey, payload));
    }
}
