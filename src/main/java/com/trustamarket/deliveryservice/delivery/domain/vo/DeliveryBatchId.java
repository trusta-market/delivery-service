package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryBatchId(UUID value) {
    public DeliveryBatchId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_BATCH_ID);
    }

    public static DeliveryBatchId generate() { return new DeliveryBatchId(UUID.randomUUID()); }
    public static DeliveryBatchId of(UUID value) { return new DeliveryBatchId(value); }
}
