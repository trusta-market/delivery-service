package com.trustamarket.deliveryservice.delivery.application.port.in;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;

public interface CreateInspectionInboundDeliveryUseCase {

    void create(CreateInspectionInboundDeliveryCommand command);
}
