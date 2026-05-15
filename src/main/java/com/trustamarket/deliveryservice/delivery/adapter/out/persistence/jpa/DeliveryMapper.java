package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.CenterId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.OrderId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ProductId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ReceiverId;
import com.trustamarket.deliveryservice.delivery.domain.vo.SenderId;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public Delivery toDomain(DeliveryJpaEntity e) {
        return Delivery.restore(
                DeliveryId.of(e.getDeliveryId()),
                e.getDeliveryType(),
                e.getCarrierType(),
                e.getOrderId() != null ? OrderId.of(e.getOrderId()) : null,
                ProductId.of(e.getProductId()),
                SenderId.of(e.getSenderId()),
                ReceiverId.of(e.getReceiverId()),
                e.getCenterId() != null ? CenterId.of(e.getCenterId()) : null,
                e.getCreatedAt(),
                e.getStatus(),
                e.getBatchId() != null ? DeliveryBatchId.of(e.getBatchId()) : null,
                e.getTrackingNumber(),
                e.getShippedAt(),
                e.getDeliveredAt(),
                e.getCancelledAt(),
                e.getFailureReason()
        );
    }
}
