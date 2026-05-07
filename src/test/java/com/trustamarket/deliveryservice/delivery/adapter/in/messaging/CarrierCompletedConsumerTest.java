package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.HandleCarrierCompletedCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.HandleCarrierCompletedUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CarrierCompletedConsumerTest {

    @Mock
    private HandleCarrierCompletedUseCase handleCarrierCompletedUseCase;

    private CarrierCompletedConsumer consumer;

    private static final UUID DELIVERY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final String VALID_PAYLOAD = """
            {
              "deliveryId": "00000000-0000-0000-0000-000000000001"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new CarrierCompletedConsumer(handleCarrierCompletedUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 HandleCarrierCompletedCommand를 담아 handle()을 호출한다")
    void consume_validPayload_callsHandle() {
        consumer.consume(VALID_PAYLOAD);

        ArgumentCaptor<HandleCarrierCompletedCommand> captor =
                ArgumentCaptor.forClass(HandleCarrierCompletedCommand.class);
        then(handleCarrierCompletedUseCase).should().handle(captor.capture());

        assertThat(captor.getValue().deliveryId()).isEqualTo(DELIVERY_ID);
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);

        then(handleCarrierCompletedUseCase).shouldHaveNoInteractions();
    }
}
