package com.trustamarket.deliveryservice.delivery.application.dto.command;

import java.util.UUID;

public record CreateInspectionInboundDeliveryCommand(
        UUID productId,
        UUID sellerId,
        UUID centerId
) {
}
