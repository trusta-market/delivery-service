package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.util.UUID;

public record CarrierDeliveryRequestedKafkaEvent(
        UUID deliveryId,
        String deliveryType,
        UUID productId,
        UUID senderId,
        UUID receiverId
) {
}
