package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.time.Instant;
import java.util.UUID;

// order-service 의 DeliveryStartedMessage 와 schema 일치 — eventId (멱등성 키) + orderId + startedAt.
// deliveryId 는 dignostics 용 (order 는 Jackson default 로 무시).
public record DeliveryStartedKafkaEvent(
        UUID eventId,
        UUID orderId,
        UUID deliveryId,
        Instant startedAt
) {
}
