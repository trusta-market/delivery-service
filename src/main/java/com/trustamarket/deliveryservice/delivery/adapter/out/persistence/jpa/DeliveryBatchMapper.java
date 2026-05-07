package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import org.springframework.stereotype.Component;

@Component
public class DeliveryBatchMapper {

    public DeliveryBatch toDomain(DeliveryBatchJpaEntity e) {
        return DeliveryBatch.restore(
                DeliveryBatchId.of(e.getDeliveryBatchId()),
                e.getStartedAt(),
                e.getStatus(),
                e.getTotalCount(),
                e.getSuccessCount(),
                e.getFailedCount(),
                e.getCompletedAt()
        );
    }
}
