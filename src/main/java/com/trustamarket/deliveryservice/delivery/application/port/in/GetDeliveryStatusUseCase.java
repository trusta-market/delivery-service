package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.result.GetDeliveryStatusResult;

import java.util.UUID;

public interface GetDeliveryStatusUseCase {

    GetDeliveryStatusResult get(UUID deliveryId);
}
