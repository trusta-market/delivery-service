package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import java.util.UUID;

record CarrierReturnCompletedKafkaEvent(
        UUID productId
) {
}
