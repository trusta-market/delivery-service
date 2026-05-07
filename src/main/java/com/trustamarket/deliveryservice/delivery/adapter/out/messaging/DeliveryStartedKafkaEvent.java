package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.util.UUID;

public record DeliveryStartedKafkaEvent(
        UUID orderId,
        UUID deliveryId
) {
}
