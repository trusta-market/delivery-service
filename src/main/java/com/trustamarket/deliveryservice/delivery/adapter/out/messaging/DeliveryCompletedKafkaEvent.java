package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.util.UUID;

record DeliveryCompletedKafkaEvent(
        UUID orderId,
        UUID deliveryId
) {
}
