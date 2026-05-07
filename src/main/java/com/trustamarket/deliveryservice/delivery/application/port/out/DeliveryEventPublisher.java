package com.trustamarket.deliveryservice.delivery.application.port.out;

 import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;

import java.util.UUID;

public interface DeliveryEventPublisher {

    void publishCarrierDeliveryCompleted(UUID productId);

    void publishCarrierReturnCompleted(UUID productId);

    void publishDeliveryCompleted(UUID orderId, UUID deliveryId);

    void publishCarrierDeliveryRequested(Delivery delivery);

    void publishDeliveryStarted(UUID orderId, UUID deliveryId);
}
