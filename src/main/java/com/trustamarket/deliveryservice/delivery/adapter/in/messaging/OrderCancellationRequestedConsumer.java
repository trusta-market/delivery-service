package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CancelOrderDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CancelOrderDeliveryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancellationRequestedConsumer {

    private final CancelOrderDeliveryUseCase cancelOrderDeliveryUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.cancellation.requested", groupId = "delivery-service")
    public void consume(String payload) {
        OrderCancellationRequestedEvent event;
        try {
            event = objectMapper.readValue(payload, OrderCancellationRequestedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("order.cancellation.requested 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        cancelOrderDeliveryUseCase.cancel(new CancelOrderDeliveryCommand(event.orderId()));
        log.info("ORDER_DELIVERY 취소 처리 완료: orderId={}", event.orderId());
    }
}
