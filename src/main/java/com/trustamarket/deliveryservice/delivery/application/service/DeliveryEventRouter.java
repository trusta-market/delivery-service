package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryEventPublisher;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryEventRouter {

    private final DeliveryEventPublisher publisher;

    public void route(Delivery delivery) {
        switch (delivery.getDeliveryType()) {
            case INSPECTION_INBOUND ->
                publisher.publishCarrierDeliveryCompleted(delivery.getProductId().value());
            case INSPECTION_RETURN ->
                publisher.publishCarrierReturnCompleted(delivery.getProductId().value());
            case ORDER_DELIVERY ->
                publisher.publishDeliveryCompleted(delivery.getOrderId().value(), delivery.getId().value());
        }
    }
}
