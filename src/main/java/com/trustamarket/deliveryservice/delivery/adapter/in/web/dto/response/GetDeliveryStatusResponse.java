package com.trustamarket.deliveryservice.delivery.adapter.in.web.dto.response;

import com.trustamarket.deliveryservice.delivery.application.dto.result.GetDeliveryStatusResult;

import java.util.UUID;

public record GetDeliveryStatusResponse(
        UUID deliveryId,
        String deliveryType,
        String status,
        UUID orderId,
        UUID productId,
        String trackingNumber
) {
    public static GetDeliveryStatusResponse from(GetDeliveryStatusResult result) {
        return new GetDeliveryStatusResponse(
                result.deliveryId(),
                result.deliveryType().name(),
                result.status().name(),
                result.orderId(),
                result.productId(),
                result.trackingNumber()
        );
    }
}
