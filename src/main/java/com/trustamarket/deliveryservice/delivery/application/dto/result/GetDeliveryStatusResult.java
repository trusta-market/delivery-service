package com.trustamarket.deliveryservice.delivery.application.dto.result;

import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;

import java.util.UUID;

public record GetDeliveryStatusResult(
        UUID deliveryId,
        DeliveryType deliveryType,
        DeliveryStatus status,
        UUID orderId,
        UUID productId,
        String trackingNumber
) {
    public static GetDeliveryStatusResult from(Delivery delivery) {
        return new GetDeliveryStatusResult(
                delivery.getId().value(),
                delivery.getDeliveryType(),
                delivery.getStatus(),
                delivery.getOrderId() != null ? delivery.getOrderId().value() : null,
                delivery.getProductId().value(),
                delivery.getTrackingNumber()
        );
    }
}
