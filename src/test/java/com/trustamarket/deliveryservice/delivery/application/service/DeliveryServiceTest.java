package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateOrderDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.HandleCarrierCompletedCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.result.GetDeliveryStatusResult;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.InspectionCenterClient;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;
import com.trustamarket.deliveryservice.delivery.application.port.out.ProcessedEventRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.CenterId;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private InspectionCenterClient inspectionCenterClient;

    @Mock
    private DeliveryEventRouter deliveryEventRouter;

    @InjectMocks
    private DeliveryService deliveryService;

    private static final UUID DELIVERY_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PRODUCT_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID SELLER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID CENTER_ID   = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID ORDER_ID    = UUID.fromString("00000000-0000-0000-0000-000000000005");
    private static final UUID BUYER_ID    = UUID.fromString("00000000-0000-0000-0000-000000000006");

    @Nested
    @DisplayName("검수 입고 배송 생성 (createInspectionInbound)")
    class CreateInspectionInbound {

        @Test
        @DisplayName("신규 이벤트면 INSPECTION_INBOUND 배송을 생성하고 저장한다")
        void create_inspectionInbound_success() {
            CreateInspectionInboundDeliveryCommand command =
                    new CreateInspectionInboundDeliveryCommand(PRODUCT_ID, SELLER_ID, CENTER_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.create(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());

            Delivery saved = captor.getValue();
            assertThat(saved.getDeliveryType()).isEqualTo(DeliveryType.INSPECTION_INBOUND);
            assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.PENDING);
            assertThat(saved.getProductId().value()).isEqualTo(PRODUCT_ID);
            assertThat(saved.getSenderId().value()).isEqualTo(SELLER_ID);
            assertThat(saved.getReceiverId().value()).isEqualTo(CENTER_ID);
            assertThat(saved.getCenterId().value()).isEqualTo(CENTER_ID);

            then(processedEventRepository).should().save("inspection.requested:" + PRODUCT_ID);
        }

        @Test
        @DisplayName("이미 처리된 이벤트면 배송 생성 없이 스킵한다")
        void create_inspectionInbound_duplicateEvent_skip() {
            CreateInspectionInboundDeliveryCommand command =
                    new CreateInspectionInboundDeliveryCommand(PRODUCT_ID, SELLER_ID, CENTER_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(true);

            deliveryService.create(command);

            then(deliveryRepository).should(never()).save(any());
            then(processedEventRepository).should(never()).save(anyString());
        }
    }

    @Nested
    @DisplayName("검수 반송 배송 생성 (createInspectionReturn)")
    class CreateInspectionReturn {

        @Test
        @DisplayName("신규 이벤트면 INSPECTION_RETURN 배송을 생성하고 저장한다")
        void create_inspectionReturn_success() {
            CreateInspectionReturnDeliveryCommand command =
                    new CreateInspectionReturnDeliveryCommand(PRODUCT_ID, SELLER_ID, CENTER_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.create(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());

            Delivery saved = captor.getValue();
            assertThat(saved.getDeliveryType()).isEqualTo(DeliveryType.INSPECTION_RETURN);
            assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.PENDING);
            assertThat(saved.getProductId().value()).isEqualTo(PRODUCT_ID);
            assertThat(saved.getSenderId().value()).isEqualTo(CENTER_ID);
            assertThat(saved.getReceiverId().value()).isEqualTo(SELLER_ID);
            assertThat(saved.getCenterId().value()).isEqualTo(CENTER_ID);

            then(processedEventRepository).should().save("inspection.failed:" + PRODUCT_ID);
        }

        @Test
        @DisplayName("이미 처리된 이벤트면 배송 생성 없이 스킵한다")
        void create_inspectionReturn_duplicateEvent_skip() {
            CreateInspectionReturnDeliveryCommand command =
                    new CreateInspectionReturnDeliveryCommand(PRODUCT_ID, SELLER_ID, CENTER_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(true);

            deliveryService.create(command);

            then(deliveryRepository).should(never()).save(any());
            then(processedEventRepository).should(never()).save(anyString());
        }
    }

    @Nested
    @DisplayName("주문 배송 생성 (createOrderDelivery)")
    class CreateOrderDelivery {

        @Test
        @DisplayName("LOW 주문이면 senderId=sellerId, centerId=null로 ORDER_DELIVERY 배송을 생성한다")
        void create_orderDelivery_low_success() {
            CreateOrderDeliveryCommand command =
                    new CreateOrderDeliveryCommand(ORDER_ID, PRODUCT_ID, SELLER_ID, BUYER_ID, "LOW");

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.create(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());

            Delivery saved = captor.getValue();
            assertThat(saved.getDeliveryType()).isEqualTo(DeliveryType.ORDER_DELIVERY);
            assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.PENDING);
            assertThat(saved.getOrderId().value()).isEqualTo(ORDER_ID);
            assertThat(saved.getProductId().value()).isEqualTo(PRODUCT_ID);
            assertThat(saved.getSenderId().value()).isEqualTo(SELLER_ID);
            assertThat(saved.getReceiverId().value()).isEqualTo(BUYER_ID);
            assertThat(saved.getCenterId()).isNull();

            then(inspectionCenterClient).shouldHaveNoInteractions();
            then(processedEventRepository).should().save("order.paid:" + ORDER_ID);
        }

        @Test
        @DisplayName("HIGH 주문이면 Feign으로 centerId를 조회하고 senderId=centerId로 배송을 생성한다")
        void create_orderDelivery_high_success() {
            CreateOrderDeliveryCommand command =
                    new CreateOrderDeliveryCommand(ORDER_ID, PRODUCT_ID, SELLER_ID, BUYER_ID, "HIGH");

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(inspectionCenterClient.getCenterIdByProductId(PRODUCT_ID)).willReturn(Optional.of(CENTER_ID));
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.create(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());

            Delivery saved = captor.getValue();
            assertThat(saved.getDeliveryType()).isEqualTo(DeliveryType.ORDER_DELIVERY);
            assertThat(saved.getSenderId().value()).isEqualTo(CENTER_ID);
            assertThat(saved.getReceiverId().value()).isEqualTo(BUYER_ID);
            assertThat(saved.getCenterId().value()).isEqualTo(CENTER_ID);
        }

        @Test
        @DisplayName("HIGH 주문에서 centerId 조회 실패 시 DeliveryException을 던진다")
        void create_orderDelivery_high_centerNotFound_throws() {
            CreateOrderDeliveryCommand command =
                    new CreateOrderDeliveryCommand(ORDER_ID, PRODUCT_ID, SELLER_ID, BUYER_ID, "HIGH");

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(inspectionCenterClient.getCenterIdByProductId(PRODUCT_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.create(command))
                    .isInstanceOf(DeliveryException.class);

            then(deliveryRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("이미 처리된 이벤트면 배송 생성 없이 스킵한다")
        void create_orderDelivery_duplicateEvent_skip() {
            CreateOrderDeliveryCommand command =
                    new CreateOrderDeliveryCommand(ORDER_ID, PRODUCT_ID, SELLER_ID, BUYER_ID, "LOW");

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(true);

            deliveryService.create(command);

            then(deliveryRepository).should(never()).save(any());
            then(processedEventRepository).should(never()).save(anyString());
        }
    }

    @Nested
    @DisplayName("배송 완료 처리 (handle)")
    class Handle {

        @Test
        @DisplayName("INSPECTION_INBOUND 배송을 완료 처리하고 라우터를 호출한다")
        void handle_inspectionInbound_success() {
            Delivery shippedDelivery = shippedDelivery(DELIVERY_ID, DeliveryType.INSPECTION_INBOUND, null);
            HandleCarrierCompletedCommand command = new HandleCarrierCompletedCommand(DELIVERY_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.of(shippedDelivery));
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.handle(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
            assertThat(captor.getValue().getDeliveredAt()).isNotNull();

            then(deliveryEventRouter).should().route(captor.getValue());
            then(processedEventRepository).should().save("carrier.completed:" + DELIVERY_ID);
        }

        @Test
        @DisplayName("ORDER_DELIVERY 배송을 완료 처리하고 라우터를 호출한다")
        void handle_orderDelivery_success() {
            Delivery shippedDelivery = shippedDelivery(DELIVERY_ID, DeliveryType.ORDER_DELIVERY, ORDER_ID);
            HandleCarrierCompletedCommand command = new HandleCarrierCompletedCommand(DELIVERY_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.of(shippedDelivery));
            given(deliveryRepository.save(any(Delivery.class))).willAnswer(i -> i.getArgument(0));

            deliveryService.handle(command);

            ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
            then(deliveryRepository).should().save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.DELIVERED);

            then(deliveryEventRouter).should().route(captor.getValue());
        }

        @Test
        @DisplayName("이미 처리된 이벤트면 스킵하고 라우터를 호출하지 않는다")
        void handle_duplicateEvent_skip() {
            HandleCarrierCompletedCommand command = new HandleCarrierCompletedCommand(DELIVERY_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(true);

            deliveryService.handle(command);

            then(deliveryRepository).should(never()).findById(any());
            then(deliveryRepository).should(never()).save(any());
            then(deliveryEventRouter).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 deliveryId면 DeliveryException을 던진다")
        void handle_deliveryNotFound_throwsException() {
            HandleCarrierCompletedCommand command = new HandleCarrierCompletedCommand(DELIVERY_ID);

            given(processedEventRepository.existsByEventKey(anyString())).willReturn(false);
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.handle(command))
                    .isInstanceOf(DeliveryException.class);

            then(deliveryEventRouter).shouldHaveNoInteractions();
            then(processedEventRepository).should(never()).save(anyString());
        }
    }

    @Nested
    @DisplayName("배송 상태 조회 (get)")
    class GetDeliveryStatus {

        @Test
        @DisplayName("존재하는 deliveryId면 GetDeliveryStatusResult를 반환한다")
        void get_success() {
            Delivery delivery = shippedDelivery(DELIVERY_ID, DeliveryType.INSPECTION_INBOUND, null);
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.of(delivery));

            GetDeliveryStatusResult result = deliveryService.get(DELIVERY_ID);

            assertThat(result.deliveryId()).isEqualTo(DELIVERY_ID);
            assertThat(result.deliveryType()).isEqualTo(DeliveryType.INSPECTION_INBOUND);
            assertThat(result.status()).isEqualTo(DeliveryStatus.SHIPPED);
            assertThat(result.productId()).isEqualTo(PRODUCT_ID);
            assertThat(result.trackingNumber()).isEqualTo("TRACK-001");
            assertThat(result.orderId()).isNull();
        }

        @Test
        @DisplayName("ORDER_DELIVERY면 orderId도 결과에 포함된다")
        void get_orderDelivery_includesOrderId() {
            Delivery delivery = shippedDelivery(DELIVERY_ID, DeliveryType.ORDER_DELIVERY, ORDER_ID);
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.of(delivery));

            GetDeliveryStatusResult result = deliveryService.get(DELIVERY_ID);

            assertThat(result.deliveryType()).isEqualTo(DeliveryType.ORDER_DELIVERY);
            assertThat(result.orderId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 deliveryId면 DeliveryException을 던진다")
        void get_notFound_throwsException() {
            given(deliveryRepository.findById(DeliveryId.of(DELIVERY_ID))).willReturn(Optional.empty());

            assertThatThrownBy(() -> deliveryService.get(DELIVERY_ID))
                    .isInstanceOf(DeliveryException.class);
        }
    }

    private Delivery shippedDelivery(UUID deliveryId, DeliveryType deliveryType, UUID orderId) {
        return Delivery.restore(
                DeliveryId.of(deliveryId),
                deliveryType,
                CarrierType.MOCK,
                orderId != null ? new OrderId(orderId) : null,
                new ProductId(PRODUCT_ID),
                new SenderId(SELLER_ID),
                new ReceiverId(BUYER_ID),
                new CenterId(CENTER_ID),
                Instant.now().minusSeconds(3600),
                DeliveryStatus.SHIPPED,
                new DeliveryBatchId(UUID.randomUUID()),
                "TRACK-001",
                Instant.now().minusSeconds(1800),
                null,
                null,
                null
        );
    }
}
