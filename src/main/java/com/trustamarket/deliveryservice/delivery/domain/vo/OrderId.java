package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_ORDER_ID);
    }

    public static OrderId of(UUID value) { return new OrderId(value); }
    public static OrderId of(String value) { return new OrderId(UUID.fromString(value)); }
}
