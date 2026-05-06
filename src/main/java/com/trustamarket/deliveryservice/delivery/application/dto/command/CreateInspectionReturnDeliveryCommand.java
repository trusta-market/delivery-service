package com.trustamarket.deliveryservice.delivery.application.dto.command;

import java.util.UUID;

public record CreateInspectionReturnDeliveryCommand(
        UUID productId,
        UUID sellerId,
        UUID centerId
) {
}
