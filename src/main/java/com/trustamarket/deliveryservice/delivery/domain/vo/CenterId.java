package com.trustamarket.deliveryservice.delivery.domain.vo;

import java.util.UUID;

public record CenterId(UUID value) {
    public static CenterId of(UUID value) { return new CenterId(value); }
    public static CenterId of(String value) { return new CenterId(UUID.fromString(value)); }
}
