package com.trustamarket.deliveryservice.delivery.application.port.out;

import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.OrderId;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(DeliveryId id);

    Optional<Delivery> findByOrderId(OrderId orderId);

    List<Delivery> findAllByStatus(DeliveryStatus status);
}
