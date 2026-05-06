package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;

public interface CreateInspectionReturnDeliveryUseCase {

    void create(CreateInspectionReturnDeliveryCommand command);
}
