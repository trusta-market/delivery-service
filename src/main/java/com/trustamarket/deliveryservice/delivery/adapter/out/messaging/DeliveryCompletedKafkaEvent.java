package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.time.Instant;
import java.util.UUID;

// order-service 의 DeliveryCompletedMessage 와 schema 일치 — eventId (멱등성 키) + orderId + completedAt.
// deliveryId 는 dignostics 용 (order 는 Jackson default 로 무시).
record DeliveryCompletedKafkaEvent(
        UUID eventId,
        UUID orderId,
        UUID deliveryId,
        Instant completedAt
) {
}
