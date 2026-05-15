package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionInboundDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.dto.command.CreateInspectionReturnDeliveryCommand;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.ProcessedEventRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @InjectMocks
    private DeliveryService deliveryService;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SELLER_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID CENTER_ID  = UUID.fromString("00000000-0000-0000-0000-000000000003");

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
}
