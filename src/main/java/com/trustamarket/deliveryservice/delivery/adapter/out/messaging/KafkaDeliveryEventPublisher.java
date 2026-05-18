package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryEventPublisher;
import com.trustamarket.deliveryservice.delivery.application.port.out.OutboxRepository;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDeliveryEventPublisher implements DeliveryEventPublisher {

    private static final String CARRIER_DELIVERY_COMPLETED_TOPIC = "carrier.delivery_completed";
    private static final String CARRIER_RETURN_COMPLETED_TOPIC = "carrier.return_completed";
    private static final String DELIVERY_COMPLETED_TOPIC = "delivery.completed";
    private static final String CARRIER_DELIVERY_REQUESTED_TOPIC = "carrier.delivery_requested";
    private static final String DELIVERY_STARTED_TOPIC = "delivery.started";

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishCarrierDeliveryCompleted(UUID productId) {
        saveToOutbox(CARRIER_DELIVERY_COMPLETED_TOPIC, productId.toString(),
                new CarrierDeliveryCompletedKafkaEvent(productId));
    }

    @Override
    public void publishCarrierReturnCompleted(UUID productId) {
        saveToOutbox(CARRIER_RETURN_COMPLETED_TOPIC, productId.toString(),
                new CarrierReturnCompletedKafkaEvent(productId));
    }

    @Override
    public void publishDeliveryCompleted(UUID orderId, UUID deliveryId) {
        // order-service 의 DeliveryCompletedListener 가 eventId (멱등성 키) 필수 — publisher 가 UUID 생성.
        saveToOutbox(DELIVERY_COMPLETED_TOPIC, orderId.toString(),
                new DeliveryCompletedKafkaEvent(UUID.randomUUID(), orderId, deliveryId, Instant.now()));
    }

    @Override
    public void publishCarrierDeliveryRequested(Delivery delivery) {
        saveToOutbox(CARRIER_DELIVERY_REQUESTED_TOPIC, delivery.getId().value().toString(),
                new CarrierDeliveryRequestedKafkaEvent(
                        delivery.getId().value(),
                        delivery.getDeliveryType().name(),
                        delivery.getProductId().value(),
                        delivery.getSenderId().value(),
                        delivery.getReceiverId().value()
                ));
    }

    @Override
    public void publishDeliveryStarted(UUID orderId, UUID deliveryId) {
        // order-service 의 DeliveryStartedListener 가 eventId (멱등성 키) 필수 — publisher 가 UUID 생성.
        saveToOutbox(DELIVERY_STARTED_TOPIC, orderId.toString(),
                new DeliveryStartedKafkaEvent(UUID.randomUUID(), orderId, deliveryId, Instant.now()));
    }

    private void saveToOutbox(String topic, String key, Object payload) {
        try {
            outboxRepository.save(topic, key, objectMapper.writeValueAsString(payload));
            log.debug("outbox 저장: topic={}, key={}", topic, key);
        } catch (JsonProcessingException e) {
            log.error("outbox 직렬화 실패: topic={}, key={}", topic, key, e);
            throw new RuntimeException(e);
        }
    }
}
