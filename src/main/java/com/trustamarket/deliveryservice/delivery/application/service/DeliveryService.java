package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionInboundDeliveryUseCase;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.ProcessedEventRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.CenterId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ProductId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ReceiverId;
import com.trustamarket.deliveryservice.delivery.domain.vo.SenderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService implements CreateInspectionInboundDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Override
    public void create(CreateInspectionInboundDeliveryCommand command) {
        String eventKey = "inspection.requested:" + command.productId();
        if (processedEventRepository.existsByEventKey(eventKey)) {
            log.warn("중복 이벤트 스킵: key={}", eventKey);
            return;
        }

        Delivery delivery = Delivery.create(
                DeliveryId.generate(),
                DeliveryType.INSPECTION_INBOUND,
                CarrierType.MOCK,
                null,
                new ProductId(command.productId()),
                new SenderId(command.sellerId()),
                new ReceiverId(command.centerId()),
                CenterId.of(command.centerId()),
                Instant.now()
        );

        deliveryRepository.save(delivery);
        processedEventRepository.save(eventKey);
        log.info("INSPECTION_INBOUND 배송 생성 완료: deliveryId={}, productId={}",
                delivery.getId().value(), command.productId());
    }
}
