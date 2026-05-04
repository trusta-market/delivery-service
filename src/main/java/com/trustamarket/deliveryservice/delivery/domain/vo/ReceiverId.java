package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record ReceiverId(UUID value) {
    public ReceiverId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_RECEIVER_ID);
    }

    public static ReceiverId of(UUID value) { return new ReceiverId(value); }
    public static ReceiverId of(String value) { return new ReceiverId(UUID.fromString(value)); }
}
