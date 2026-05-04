package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record DeliveryId(UUID value) {
    public DeliveryId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_DELIVERY_ID);
    }

    public static DeliveryId generate() { return new DeliveryId(UUID.randomUUID()); }
    public static DeliveryId of(UUID value) { return new DeliveryId(value); }
    public static DeliveryId of(String value) { return new DeliveryId(UUID.fromString(value)); }
}
