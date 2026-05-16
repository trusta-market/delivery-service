package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CancelOrderDeliveryCommand;

public interface CancelOrderDeliveryUseCase {

    void cancel(CancelOrderDeliveryCommand command);
}
