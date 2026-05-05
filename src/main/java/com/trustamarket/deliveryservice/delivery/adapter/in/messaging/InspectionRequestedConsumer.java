package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionInboundDeliveryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionRequestedConsumer {

    private final CreateInspectionInboundDeliveryUseCase createInspectionInboundDeliveryUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inspection.requested", groupId = "delivery-service")
    public void consume(String payload) {
        InspectionRequestedEvent event;
        try {
            event = objectMapper.readValue(payload, InspectionRequestedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("inspection.requested 역직렬화 실패: payload={}", payload, e);
            throw new RuntimeException(e);
        }

        createInspectionInboundDeliveryUseCase.create(new CreateInspectionInboundDeliveryCommand(
                event.productId(),
                event.sellerId(),
                event.centerId()
        ));
        log.info("INSPECTION_INBOUND 배송 생성 처리 완료: productId={}", event.productId());
    }
}
