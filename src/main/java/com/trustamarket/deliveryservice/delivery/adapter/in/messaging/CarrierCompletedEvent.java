package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import java.util.UUID;

record CarrierCompletedEvent(
        UUID deliveryId
) {
}
