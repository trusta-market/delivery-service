package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record SenderId(UUID value) {
    public SenderId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_SENDER_ID);
    }

    public static SenderId of(UUID value) { return new SenderId(value); }
    public static SenderId of(String value) { return new SenderId(UUID.fromString(value)); }
}
