package com.trustamarket.deliveryservice.delivery.domain.vo;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;

import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        if (value == null) throw new DeliveryException(DeliveryErrorCode.INVALID_PRODUCT_ID);
    }

    public static ProductId of(UUID value) { return new ProductId(value); }
    public static ProductId of(String value) { return new ProductId(UUID.fromString(value)); }
}
