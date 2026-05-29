package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa.DeliveryOutboxJpaEntity;
import com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa.DeliveryOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxClaimAdapter {

    private final DeliveryOutboxJpaRepository outboxJpaRepository;

    @Transactional
    public List<DeliveryOutboxJpaEntity> claim(int batchSize) {
        List<DeliveryOutboxJpaEntity> pending =
                outboxJpaRepository.findPendingForUpdateSkipLocked(batchSize);
        if (pending.isEmpty()) {
            return pending;
        }
        pending.forEach(DeliveryOutboxJpaEntity::markInProgress);
        outboxJpaRepository.saveAll(pending);
        return pending;
    }
}
