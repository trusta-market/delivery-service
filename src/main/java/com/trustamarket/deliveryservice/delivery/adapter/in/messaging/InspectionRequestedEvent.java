package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import java.util.UUID;

record InspectionRequestedEvent(
        UUID productId,
        UUID sellerId,
        UUID centerId,
        long originalPriceAmount,
        String currency
) {
}
