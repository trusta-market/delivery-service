package com.trustamarket.deliveryservice.delivery.application.dto.command;

import java.util.UUID;

public record CreateOrderDeliveryCommand(
        UUID orderId,
        UUID productId,
        UUID sellerId,
        UUID buyerId,
        String orderType
) {
}
