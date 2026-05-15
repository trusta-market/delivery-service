package com.trustamarket.deliveryservice.delivery.domain.model;

import com.trustamarket.deliveryservice.delivery.domain.enums.BatchStatus;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class DeliveryBatch {

    private final DeliveryBatchId id;
    private final Instant startedAt;

    private BatchStatus status;
    private int totalCount;
    private int successCount;
    private int failedCount;
    private Instant completedAt;

    private DeliveryBatch(DeliveryBatchId id, Instant startedAt, BatchStatus status, int totalCount) {
        this.id = Objects.requireNonNull(id);
        this.startedAt = Objects.requireNonNull(startedAt);
        this.status = Objects.requireNonNull(status);
        this.totalCount = totalCount;
    }

    private DeliveryBatch(
            DeliveryBatchId id,
            Instant startedAt,
            BatchStatus status,
            int totalCount,
            int successCount,
            int failedCount,
            Instant completedAt
    ) {
        this(id, startedAt, status, totalCount);
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.completedAt = completedAt;
    }

    public static DeliveryBatch prepare(DeliveryBatchId id, Instant startedAt) {
        return new DeliveryBatch(id, startedAt, BatchStatus.PREPARING, 0);
    }

    public void startProcessing(int totalCount) {
        this.totalCount = totalCount;
        this.status = BatchStatus.PROCESSING;
    }

    public void complete(int successCount, int failedCount, Instant completedAt) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.completedAt = Objects.requireNonNull(completedAt);
        this.status = (failedCount == 0) ? BatchStatus.COMPLETED : BatchStatus.FAILED;
    }

    public static DeliveryBatch restore(
            DeliveryBatchId id,
            Instant startedAt,
            BatchStatus status,
            int totalCount,
            int successCount,
            int failedCount,
            Instant completedAt
    ) {
        return new DeliveryBatch(id, startedAt, status, totalCount, successCount, failedCount, completedAt);
    }
}
