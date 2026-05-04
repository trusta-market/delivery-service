package com.trustamarket.deliveryservice.delivery.domain.model;

import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;
import com.trustamarket.deliveryservice.delivery.domain.vo.CenterId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.OrderId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ProductId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ReceiverId;
import com.trustamarket.deliveryservice.delivery.domain.vo.SenderId;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class Delivery {

    private final DeliveryId id;
    private final DeliveryType deliveryType;
    private final CarrierType carrierType;

    private final OrderId orderId;       // ORDER_DELIVERY, ORDER_RETURN 시 존재
    private final ProductId productId;
    private final SenderId senderId;
    private final ReceiverId receiverId;
    private final CenterId centerId;     // INSPECTION_INBOUND, ORDER_DELIVERY(HIGH), INSPECTION_RETURN 시 존재

    private final Instant createdAt;

    private DeliveryStatus status;
    private DeliveryBatchId batchId;     // 배치 실행 시 설정
    private String trackingNumber;       // carrier 발급 운송장 번호
    private Instant shippedAt;
    private Instant deliveredAt;
    private Instant cancelledAt;
    private String failureReason;

    private Delivery(
            DeliveryId id,
            DeliveryType deliveryType,
            CarrierType carrierType,
            OrderId orderId,
            ProductId productId,
            SenderId senderId,
            ReceiverId receiverId,
            CenterId centerId,
            Instant createdAt,
            DeliveryStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.deliveryType = Objects.requireNonNull(deliveryType);
        this.carrierType = Objects.requireNonNull(carrierType);
        this.orderId = orderId;
        this.productId = Objects.requireNonNull(productId);
        this.senderId = Objects.requireNonNull(senderId);
        this.receiverId = Objects.requireNonNull(receiverId);
        this.centerId = centerId;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = Objects.requireNonNull(status);
    }

    private Delivery(
            DeliveryId id,
            DeliveryType deliveryType,
            CarrierType carrierType,
            OrderId orderId,
            ProductId productId,
            SenderId senderId,
            ReceiverId receiverId,
            CenterId centerId,
            Instant createdAt,
            DeliveryStatus status,
            DeliveryBatchId batchId,
            String trackingNumber,
            Instant shippedAt,
            Instant deliveredAt,
            Instant cancelledAt,
            String failureReason
    ) {
        this(id, deliveryType, carrierType, orderId, productId, senderId, receiverId, centerId, createdAt, status);
        this.batchId = batchId;
        this.trackingNumber = trackingNumber;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.cancelledAt = cancelledAt;
        this.failureReason = failureReason;
    }

    public static Delivery create(
            DeliveryId id,
            DeliveryType deliveryType,
            CarrierType carrierType,
            OrderId orderId,
            ProductId productId,
            SenderId senderId,
            ReceiverId receiverId,
            CenterId centerId,
            Instant createdAt
    ) {
        return new Delivery(id, deliveryType, carrierType, orderId, productId, senderId, receiverId, centerId,
                createdAt, DeliveryStatus.PENDING);
    }

    public void ship(DeliveryBatchId batchId, String trackingNumber, Instant shippedAt) {
        requireStatus(DeliveryStatus.PENDING, "배송 요청");
        this.batchId = Objects.requireNonNull(batchId);
        this.trackingNumber = trackingNumber;
        this.shippedAt = Objects.requireNonNull(shippedAt);
        this.status = DeliveryStatus.SHIPPED;
    }

    public void deliver(Instant deliveredAt) {
        requireStatus(DeliveryStatus.SHIPPED, "배송 완료");
        this.deliveredAt = Objects.requireNonNull(deliveredAt);
        this.status = DeliveryStatus.DELIVERED;
    }

    public void cancel(Instant cancelledAt) {
        if (this.status != DeliveryStatus.PENDING && this.status != DeliveryStatus.SHIPPED) {
            throw new DeliveryException(DeliveryErrorCode.INVALID_STATUS_TRANSITION,
                    "취소: " + this.status + " 상태에서는 취소할 수 없습니다");
        }
        this.cancelledAt = Objects.requireNonNull(cancelledAt);
        this.status = DeliveryStatus.CANCELLED;
    }

    public void fail(String failureReason, Instant at) {
        requireStatus(DeliveryStatus.SHIPPED, "배송 실패");
        this.failureReason = failureReason;
        this.status = DeliveryStatus.FAILED;
    }

    private void requireStatus(DeliveryStatus expected, String action) {
        if (this.status != expected) {
            throw new DeliveryException(DeliveryErrorCode.INVALID_STATUS_TRANSITION,
                    action + ": expected=" + expected + ", actual=" + this.status);
        }
    }

    public static Delivery restore(
            DeliveryId id,
            DeliveryType deliveryType,
            CarrierType carrierType,
            OrderId orderId,
            ProductId productId,
            SenderId senderId,
            ReceiverId receiverId,
            CenterId centerId,
            Instant createdAt,
            DeliveryStatus status,
            DeliveryBatchId batchId,
            String trackingNumber,
            Instant shippedAt,
            Instant deliveredAt,
            Instant cancelledAt,
            String failureReason
    ) {
        return new Delivery(id, deliveryType, carrierType, orderId, productId, senderId, receiverId, centerId,
                createdAt, status, batchId, trackingNumber, shippedAt, deliveredAt, cancelledAt, failureReason);
    }
}
