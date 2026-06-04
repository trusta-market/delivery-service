package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.util.UUID;

record CarrierDeliveryCompletedKafkaEvent(
        UUID eventId,
        UUID productId
) {
}
