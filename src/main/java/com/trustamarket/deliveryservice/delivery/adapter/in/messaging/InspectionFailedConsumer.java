package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionReturnDeliveryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionFailedConsumer {

    private final CreateInspectionReturnDeliveryUseCase createInspectionReturnDeliveryUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.failed", groupId = "delivery-service")
    public void consume(String payload) {
        InspectionFailedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionFailedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.failed 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        createInspectionReturnDeliveryUseCase.create(new CreateInspectionReturnDeliveryCommand(
                event.productId(),
                event.sellerId(),
                event.centerId()
        ));
        log.info("INSPECTION_RETURN 배송 생성 처리 완료: productId={}", event.productId());
    }
}
