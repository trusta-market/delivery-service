package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import java.time.Instant;
import java.util.UUID;

record OrderCancellationRequestedEvent(
        UUID eventId,
        UUID orderId,
        UUID buyerId,
        long cancelledAmount,
        Instant cancelledAt
) {
}
