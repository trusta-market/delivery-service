package com.trustamarket.deliveryservice.delivery.application.port.out;

import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;

public interface DeliveryBatchRepository {

    DeliveryBatch save(DeliveryBatch batch);
}
