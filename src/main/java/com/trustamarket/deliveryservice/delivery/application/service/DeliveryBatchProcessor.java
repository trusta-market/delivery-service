package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryEventPublisher;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryBatchProcessor {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(Delivery delivery, DeliveryBatchId batchId, Instant now) {
        delivery.ship(batchId, null, now);
        deliveryRepository.save(delivery);
        eventPublisher.publishCarrierDeliveryRequested(delivery);
        if (delivery.getDeliveryType() == DeliveryType.ORDER_DELIVERY) {
            eventPublisher.publishDeliveryStarted(delivery.getOrderId().value(), delivery.getId().value());
        }
        log.info("배송 처리 완료: deliveryId={}, type={}", delivery.getId().value(), delivery.getDeliveryType());
    }
}
