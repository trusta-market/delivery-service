package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import java.util.UUID;

record OrderPaidEvent(
        UUID orderId,
        UUID productId,
        UUID sellerId,
        UUID buyerId,
        String orderType
) {
}
