package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateOrderDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateOrderDeliveryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final CreateOrderDeliveryUseCase createOrderDeliveryUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.paid", groupId = "delivery-service")
    public void consume(String payload) {
        OrderPaidEvent event;
        try {
            event = objectMapper.readValue(payload, OrderPaidEvent.class);
        } catch (JsonProcessingException e) {
            log.error("order.paid 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        createOrderDeliveryUseCase.create(new CreateOrderDeliveryCommand(
                event.orderId(),
                event.productId(),
                event.sellerId(),
                event.buyerId(),
                event.orderType()
        ));
        log.info("ORDER_DELIVERY 배송 생성 처리 완료: orderId={}", event.orderId());
    }
}
