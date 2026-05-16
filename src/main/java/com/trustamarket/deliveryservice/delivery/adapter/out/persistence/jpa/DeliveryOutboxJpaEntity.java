package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryOutboxJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String messageKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    private int retryCount;

    @Column(columnDefinition = "text")
    private String lastError;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;

    private DeliveryOutboxJpaEntity(UUID id, String topic, String messageKey, String payload) {
        this.id = id;
        this.topic = topic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
    }

    public static DeliveryOutboxJpaEntity of(String topic, String messageKey, String payload) {
        return new DeliveryOutboxJpaEntity(UUID.randomUUID(), topic, messageKey, payload);
    }

    public void markInProgress() {
        this.status = OutboxStatus.IN_PROGRESS;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    public void recordFailure(String error, int maxRetries) {
        this.retryCount++;
        this.lastError = error;
        this.status = this.retryCount >= maxRetries ? OutboxStatus.FAILED : OutboxStatus.PENDING;
    }
}
