package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateOrderDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionInboundDeliveryUseCase;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionReturnDeliveryUseCase;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateOrderDeliveryUseCase;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.InspectionCenterClient;
import com.trustamarket.deliveryservice.delivery.application.port.out.ProcessedEventRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.CenterId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.OrderId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ProductId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ReceiverId;
import com.trustamarket.deliveryservice.delivery.domain.vo.SenderId;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService implements CreateInspectionInboundDeliveryUseCase, CreateInspectionReturnDeliveryUseCase, CreateOrderDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final InspectionCenterClient inspectionCenterClient;

    private static final String HIGH_TYPE = "HIGH";

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

    @Override
    public void create(CreateInspectionReturnDeliveryCommand command) {
        String eventKey = "inspection.failed:" + command.productId();
        if (processedEventRepository.existsByEventKey(eventKey)) {
            log.warn("중복 이벤트 스킵: key={}", eventKey);
            return;
        }

        Delivery delivery = Delivery.create(
                DeliveryId.generate(),
                DeliveryType.INSPECTION_RETURN,
                CarrierType.MOCK,
                null,
                new ProductId(command.productId()),
                new SenderId(command.centerId()),
                new ReceiverId(command.sellerId()),
                CenterId.of(command.centerId()),
                Instant.now()
        );

        deliveryRepository.save(delivery);
        processedEventRepository.save(eventKey);
        log.info("INSPECTION_RETURN 배송 생성 완료: deliveryId={}, productId={}",
                delivery.getId().value(), command.productId());
    }

    @Override
    public void create(CreateOrderDeliveryCommand command) {
        String eventKey = "order.paid:" + command.orderId();
        if (processedEventRepository.existsByEventKey(eventKey)) {
            log.warn("중복 이벤트 스킵: key={}", eventKey);
            return;
        }

        UUID senderUuid;
        CenterId centerId;

        if (HIGH_TYPE.equals(command.orderType())) {
            UUID centerUuid = inspectionCenterClient.getCenterIdByProductId(command.productId())
                    .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.CENTER_NOT_FOUND,
                            "productId=" + command.productId()));
            senderUuid = centerUuid;
            centerId = CenterId.of(centerUuid);
        } else {
            senderUuid = command.sellerId();
            centerId = null;
        }

        Delivery delivery = Delivery.create(
                DeliveryId.generate(),
                DeliveryType.ORDER_DELIVERY,
                CarrierType.MOCK,
                new OrderId(command.orderId()),
                new ProductId(command.productId()),
                new SenderId(senderUuid),
                new ReceiverId(command.buyerId()),
                centerId,
                Instant.now()
        );

        deliveryRepository.save(delivery);
        processedEventRepository.save(eventKey);
        log.info("ORDER_DELIVERY 배송 생성 완료: deliveryId={}, orderId={}, orderType={}",
                delivery.getId().value(), command.orderId(), command.orderType());
    }
}
