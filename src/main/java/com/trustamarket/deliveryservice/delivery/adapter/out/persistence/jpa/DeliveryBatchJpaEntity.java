package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.domain.enums.BatchStatus;
import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_batches")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryBatchJpaEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID deliveryBatchId;

    @Column(nullable = false)
    private Instant startedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    private int totalCount;
    private int successCount;
    private int failedCount;

    private Instant completedAt;

    @Transient
    private boolean isNew = true;

    private DeliveryBatchJpaEntity(UUID value, Instant startedAt, BatchStatus status, int totalCount, int successCount, int failedCount, Instant completedAt) {
        this.deliveryBatchId = value;
        this.startedAt = startedAt;
        this.status = status;
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.completedAt = completedAt;
    }

    @Override
    public UUID getId() {
        return deliveryBatchId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    public static DeliveryBatchJpaEntity of(DeliveryBatch batch) {
        return new DeliveryBatchJpaEntity(
                batch.getId().value(),
                batch.getStartedAt(),
                batch.getStatus(),
                batch.getTotalCount(),
                batch.getSuccessCount(),
                batch.getFailedCount(),
                batch.getCompletedAt()
        );
    }

    public void update(DeliveryBatch batch) {
        this.status = batch.getStatus();
        this.totalCount = batch.getTotalCount();
        this.successCount = batch.getSuccessCount();
        this.failedCount = batch.getFailedCount();
        this.completedAt = batch.getCompletedAt();
    }
}
