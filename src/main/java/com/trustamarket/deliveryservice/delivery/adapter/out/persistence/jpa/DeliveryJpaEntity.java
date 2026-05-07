package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_deliveries")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryJpaEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarrierType carrierType;

    @Column(columnDefinition = "uuid")
    private UUID orderId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID senderId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID receiverId;

    @Column(columnDefinition = "uuid")
    private UUID centerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(columnDefinition = "uuid")
    private UUID batchId;

    private String trackingNumber;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant cancelledAt;
    private String failureReason;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return deliveryId;
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

    public static DeliveryJpaEntity of(Delivery delivery) {
        DeliveryJpaEntity e = new DeliveryJpaEntity();
        e.deliveryId = delivery.getId().value();
        e.deliveryType = delivery.getDeliveryType();
        e.carrierType = delivery.getCarrierType();
        e.orderId = delivery.getOrderId() != null ? delivery.getOrderId().value() : null;
        e.productId = delivery.getProductId().value();
        e.senderId = delivery.getSenderId().value();
        e.receiverId = delivery.getReceiverId().value();
        e.centerId = delivery.getCenterId() != null ? delivery.getCenterId().value() : null;
        e.status = delivery.getStatus();
        e.batchId = delivery.getBatchId() != null ? delivery.getBatchId().value() : null;
        e.trackingNumber = delivery.getTrackingNumber();
        e.createdAt = delivery.getCreatedAt();
        e.shippedAt = delivery.getShippedAt();
        e.deliveredAt = delivery.getDeliveredAt();
        e.cancelledAt = delivery.getCancelledAt();
        e.failureReason = delivery.getFailureReason();
        return e;
    }

    public void update(Delivery delivery) {
        this.status = delivery.getStatus();
        this.batchId = delivery.getBatchId() != null ? delivery.getBatchId().value() : null;
        this.trackingNumber = delivery.getTrackingNumber();
        this.shippedAt = delivery.getShippedAt();
        this.deliveredAt = delivery.getDeliveredAt();
        this.cancelledAt = delivery.getCancelledAt();
        this.failureReason = delivery.getFailureReason();
    }
}
