package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryEventPublisher;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import com.trustamarket.deliveryservice.delivery.domain.vo.OrderId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ProductId;
import com.trustamarket.deliveryservice.delivery.domain.vo.ReceiverId;
import com.trustamarket.deliveryservice.delivery.domain.vo.SenderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DeliveryBatchProcessorTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private DeliveryEventPublisher eventPublisher;

    @InjectMocks private DeliveryBatchProcessor processor;

    private static final UUID DELIVERY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PRODUCT_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SELLER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID BUYER_ID    = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID ORDER_ID    = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID BATCH_ID    = UUID.fromString("00000000-0000-0000-0000-000000000006");

    @Nested
    @DisplayName("배송 처리 (process)")
    class Process {

        @Test
        @DisplayName("INSPECTION_INBOUND 배송은 SHIPPED로 변경하고 carrier 요청 이벤트를 발행한다")
        void process_inspectionInbound_shipsAndPublishesCarrierRequest() {
            Delivery delivery = pendingDelivery(DELIVERY_ID, DeliveryType.INSPECTION_INBOUND, null);
            given(deliveryRepository.save(any())).willAnswer(i -> i.getArgument(0));

            processor.process(delivery, DeliveryBatchId.of(BATCH_ID), Instant.now());

            assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
            assertThat(delivery.getBatchId().value()).isEqualTo(BATCH_ID);
            assertThat(delivery.getShippedAt()).isNotNull();

            then(deliveryRepository).should().save(delivery);
            then(eventPublisher).should().publishCarrierDeliveryRequested(delivery);
            then(eventPublisher).should(never()).publishDeliveryStarted(any(), any());
        }

        @Test
        @DisplayName("ORDER_DELIVERY 배송은 carrier 요청과 delivery.started를 함께 발행한다")
        void process_orderDelivery_publishesCarrierRequestAndDeliveryStarted() {
            Delivery delivery = pendingDelivery(DELIVERY_ID, DeliveryType.ORDER_DELIVERY, ORDER_ID);
            given(deliveryRepository.save(any())).willAnswer(i -> i.getArgument(0));

            processor.process(delivery, DeliveryBatchId.of(BATCH_ID), Instant.now());

            assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);

            then(eventPublisher).should().publishCarrierDeliveryRequested(delivery);
            then(eventPublisher).should().publishDeliveryStarted(ORDER_ID, DELIVERY_ID);
        }

        @Test
        @DisplayName("INSPECTION_RETURN 배송은 carrier 요청만 발행하고 delivery.started는 발행하지 않는다")
        void process_inspectionReturn_publishesOnlyCarrierRequest() {
            Delivery delivery = pendingDelivery(DELIVERY_ID, DeliveryType.INSPECTION_RETURN, null);
            given(deliveryRepository.save(any())).willAnswer(i -> i.getArgument(0));

            processor.process(delivery, DeliveryBatchId.of(BATCH_ID), Instant.now());

            then(eventPublisher).should().publishCarrierDeliveryRequested(delivery);
            then(eventPublisher).should(never()).publishDeliveryStarted(any(), any());
        }

        @Test
        @DisplayName("PENDING이 아닌 배송은 DeliveryException을 던지고 저장 및 이벤트 발행을 하지 않는다")
        void process_nonPendingDelivery_throwsDeliveryException() {
            Delivery shippedDelivery = Delivery.restore(
                    DeliveryId.of(DELIVERY_ID),
                    DeliveryType.INSPECTION_INBOUND,
                    CarrierType.MOCK,
                    null,
                    new ProductId(PRODUCT_ID),
                    new SenderId(SELLER_ID),
                    new ReceiverId(BUYER_ID),
                    null,
                    Instant.now(),
                    DeliveryStatus.SHIPPED,
                    DeliveryBatchId.of(BATCH_ID), null,
                    Instant.now(), null, null, null
            );

            assertThatThrownBy(() -> processor.process(shippedDelivery, DeliveryBatchId.of(BATCH_ID), Instant.now()))
                    .isInstanceOf(DeliveryException.class);

            then(deliveryRepository).should(never()).save(any());
            then(eventPublisher).shouldHaveNoInteractions();
        }
    }

    private Delivery pendingDelivery(UUID deliveryId, DeliveryType deliveryType, UUID orderId) {
        return Delivery.restore(
                DeliveryId.of(deliveryId),
                deliveryType,
                CarrierType.MOCK,
                orderId != null ? new OrderId(orderId) : null,
                new ProductId(PRODUCT_ID),
                new SenderId(SELLER_ID),
                new ReceiverId(BUYER_ID),
                null,
                Instant.now(),
                DeliveryStatus.PENDING,
                null, null, null, null, null, null
        );
    }
}
