package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import java.util.UUID;

record InspectionFailedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId
) {
}
