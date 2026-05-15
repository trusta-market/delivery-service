package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.command.HandleCarrierCompletedCommand;

public interface HandleCarrierCompletedUseCase {

    void handle(HandleCarrierCompletedCommand command);
}
