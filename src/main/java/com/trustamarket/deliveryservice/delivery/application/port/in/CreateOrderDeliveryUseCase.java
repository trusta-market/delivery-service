package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateOrderDeliveryCommand;

public interface CreateOrderDeliveryUseCase {

    void create(CreateOrderDeliveryCommand command);
}
