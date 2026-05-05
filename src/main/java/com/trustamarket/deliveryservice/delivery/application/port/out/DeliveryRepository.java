package com.trustamarket.deliveryservice.delivery.application.port.out;

import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;

import java.util.Optional;

public interface DeliveryRepository {
    Delivery save(Delivery delivery);
    Optional<Delivery> findById(DeliveryId id);
}
