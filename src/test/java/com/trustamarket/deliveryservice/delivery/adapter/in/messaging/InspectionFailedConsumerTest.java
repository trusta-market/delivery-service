package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateInspectionReturnDeliveryUseCase;
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
class InspectionFailedConsumerTest {

    @Mock
    private CreateInspectionReturnDeliveryUseCase createInspectionReturnDeliveryUseCase;

    private InspectionFailedConsumer consumer;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID CENTER_ID  = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private static final String VALID_PAYLOAD = """
            {
              "productId": "00000000-0000-0000-0000-000000000001",
              "sellerId": "00000000-0000-0000-0000-000000000002",
              "centerId": "00000000-0000-0000-0000-000000000003"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new InspectionFailedConsumer(createInspectionReturnDeliveryUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 CreateInspectionReturnDeliveryCommand를 담아 create()를 호출한다")
    void consume_validPayload_callsCreate() {
        consumer.consume(VALID_PAYLOAD);

        ArgumentCaptor<CreateInspectionReturnDeliveryCommand> captor =
                ArgumentCaptor.forClass(CreateInspectionReturnDeliveryCommand.class);
        then(createInspectionReturnDeliveryUseCase).should().create(captor.capture());

        CreateInspectionReturnDeliveryCommand cmd = captor.getValue();
        assertThat(cmd.productId()).isEqualTo(PRODUCT_ID);
        assertThat(cmd.sellerId()).isEqualTo(SELLER_ID);
        assertThat(cmd.centerId()).isEqualTo(CENTER_ID);
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);
        then(createInspectionReturnDeliveryUseCase).shouldHaveNoInteractions();
    }
}
