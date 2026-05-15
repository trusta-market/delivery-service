package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryBatchRepository;
import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class DeliveryBatchRepositoryImpl implements DeliveryBatchRepository {

    private final DeliveryBatchJpaRepository jpaRepository;
    private final DeliveryBatchMapper mapper;

    @Override
    @Transactional
    public DeliveryBatch save(DeliveryBatch batch) {
        DeliveryBatchJpaEntity entity = jpaRepository.findById(batch.getId().value())
                .map(existing -> {
                    existing.update(batch);
                    return existing;
                })
                .orElseGet(() -> DeliveryBatchJpaEntity.of(batch));
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
