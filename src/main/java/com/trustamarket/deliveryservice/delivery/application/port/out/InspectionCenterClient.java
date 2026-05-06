package com.trustamarket.deliveryservice.delivery.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface InspectionCenterClient {

    Optional<UUID> getCenterIdByProductId(UUID productId);
}
