package com.trustamarket.deliveryservice.delivery.application.port.out;

import java.util.UUID;

public interface DeliveryEventPublisher {

    void publishCarrierDeliveryCompleted(UUID productId);

    void publishCarrierReturnCompleted(UUID productId);

    void publishDeliveryCompleted(UUID orderId, UUID deliveryId);
}
