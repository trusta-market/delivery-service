package com.trustamarket.deliveryservice.delivery.application.dto.command;

import java.util.UUID;

public record CancelOrderDeliveryCommand(
        UUID orderId
) {
}
