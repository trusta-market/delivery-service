package com.trustamarket.deliveryservice.delivery.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateOrderDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.in.CreateOrderDeliveryUseCase;
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
class OrderPaidConsumerTest {

    @Mock
    private CreateOrderDeliveryUseCase createOrderDeliveryUseCase;

    private OrderPaidConsumer consumer;

    private static final UUID ORDER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SELLER_ID  = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID BUYER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000004");

    private static final String VALID_PAYLOAD = """
            {
              "orderId":   "00000000-0000-0000-0000-000000000001",
              "productId": "00000000-0000-0000-0000-000000000002",
              "sellerId":  "00000000-0000-0000-0000-000000000003",
              "buyerId":   "00000000-0000-0000-0000-000000000004",
              "orderType": "LOW"
            }
            """;

    @BeforeEach
    void setUp() {
        consumer = new OrderPaidConsumer(createOrderDeliveryUseCase, new ObjectMapper());
    }

    @Test
    @DisplayName("유효한 페이로드를 수신하면 CreateOrderDeliveryCommand를 담아 create()를 호출한다")
    void consume_validPayload_callsCreate() {
        consumer.consume(VALID_PAYLOAD);

        ArgumentCaptor<CreateOrderDeliveryCommand> captor =
                ArgumentCaptor.forClass(CreateOrderDeliveryCommand.class);
        then(createOrderDeliveryUseCase).should().create(captor.capture());

        CreateOrderDeliveryCommand cmd = captor.getValue();
        assertThat(cmd.orderId()).isEqualTo(ORDER_ID);
        assertThat(cmd.productId()).isEqualTo(PRODUCT_ID);
        assertThat(cmd.sellerId()).isEqualTo(SELLER_ID);
        assertThat(cmd.buyerId()).isEqualTo(BUYER_ID);
        assertThat(cmd.orderType()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 RuntimeException을 던지고 UseCase를 호출하지 않는다")
    void consume_invalidJson_throwsRuntimeException() {
        assertThatThrownBy(() -> consumer.consume("invalid json"))
                .isInstanceOf(RuntimeException.class);
        then(createOrderDeliveryUseCase).shouldHaveNoInteractions();
    }
}
