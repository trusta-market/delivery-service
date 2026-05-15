package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryEventPublisher;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publishCarrierDeliveryCompleted(UUID productId) {
        publish(CARRIER_DELIVERY_COMPLETED_TOPIC, productId.toString(),
                new CarrierDeliveryCompletedKafkaEvent(productId));
    }

    @Override
    public void publishCarrierReturnCompleted(UUID productId) {
        publish(CARRIER_RETURN_COMPLETED_TOPIC, productId.toString(),
                new CarrierReturnCompletedKafkaEvent(productId));
    }

    @Override
    public void publishDeliveryCompleted(UUID orderId, UUID deliveryId) {
        publish(DELIVERY_COMPLETED_TOPIC, orderId.toString(),
                new DeliveryCompletedKafkaEvent(orderId, deliveryId));
    }

    @Override
    public void publishCarrierDeliveryRequested(Delivery delivery) {
        publish(CARRIER_DELIVERY_REQUESTED_TOPIC, delivery.getId().value().toString(),
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
        publish(DELIVERY_STARTED_TOPIC, orderId.toString(),
                new DeliveryStartedKafkaEvent(orderId, deliveryId));
    }

    private void publish(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload));
            log.info("{} 발행: key={}", topic, key);
        } catch (JsonProcessingException e) {
            log.error("{} 직렬화 실패: key={}", topic, key, e);
            throw new RuntimeException(e);
        }
    }
}
