package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.HandleCarrierCompletedCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.HandleCarrierCompletedUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarrierCompletedConsumer {

    private final HandleCarrierCompletedUseCase handleCarrierCompletedUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "carrier.completed", groupId = "delivery-service")
    public void consume(String payload) {
        CarrierCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, CarrierCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("carrier.completed 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        handleCarrierCompletedUseCase.handle(new HandleCarrierCompletedCommand(event.deliveryId()));
        log.info("carrier.completed 처리 완료: deliveryId={}", event.deliveryId());
    }
}
