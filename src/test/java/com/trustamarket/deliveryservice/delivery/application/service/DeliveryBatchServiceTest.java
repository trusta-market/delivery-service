package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryBatchRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.BatchStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.CarrierType;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryType;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DeliveryBatchServiceTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private DeliveryBatchRepository deliveryBatchRepository;
    @Mock private DeliveryBatchProcessor processor;

    @InjectMocks private DeliveryBatchService deliveryBatchService;

    private static final UUID DELIVERY_ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID DELIVERY_ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID PRODUCT_ID    = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID SELLER_ID     = UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final UUID BUYER_ID      = UUID.fromString("00000000-0000-0000-0000-000000000005");

    @Nested
    @DisplayName("배송 배치 실행 (run)")
    class Run {

        @Test
        @DisplayName("PENDING 배송이 있으면 모두 processor에 위임하고 배치를 COMPLETED로 완료한다")
        void run_withPendingDeliveries_completesSuccessfully() {
            List<Delivery> pending = List.of(
                    pendingDelivery(DELIVERY_ID_1, DeliveryType.INSPECTION_INBOUND, null),
                    pendingDelivery(DELIVERY_ID_2, DeliveryType.ORDER_DELIVERY, UUID.randomUUID())
            );
            given(deliveryRepository.findAllByStatus(DeliveryStatus.PENDING)).willReturn(pending);
            given(deliveryBatchRepository.save(any())).willAnswer(i -> i.getArgument(0));

            deliveryBatchService.run();

            then(processor).should(times(2)).process(any(Delivery.class), any(DeliveryBatchId.class), any(Instant.class));

            ArgumentCaptor<DeliveryBatch> captor = ArgumentCaptor.forClass(DeliveryBatch.class);
            then(deliveryBatchRepository).should(times(3)).save(captor.capture());

            DeliveryBatch finalBatch = captor.getValue();
            assertThat(finalBatch.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            assertThat(finalBatch.getTotalCount()).isEqualTo(2);
            assertThat(finalBatch.getSuccessCount()).isEqualTo(2);
            assertThat(finalBatch.getFailedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("PENDING 배송이 없으면 processor 호출 없이 배치를 COMPLETED로 완료한다")
        void run_noPendingDeliveries_completesWithZeroCounts() {
            given(deliveryRepository.findAllByStatus(DeliveryStatus.PENDING)).willReturn(List.of());
            given(deliveryBatchRepository.save(any())).willAnswer(i -> i.getArgument(0));

            deliveryBatchService.run();

            then(processor).shouldHaveNoInteractions();

            ArgumentCaptor<DeliveryBatch> captor = ArgumentCaptor.forClass(DeliveryBatch.class);
            then(deliveryBatchRepository).should(times(3)).save(captor.capture());

            DeliveryBatch finalBatch = captor.getValue();
            assertThat(finalBatch.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            assertThat(finalBatch.getTotalCount()).isEqualTo(0);
            assertThat(finalBatch.getSuccessCount()).isEqualTo(0);
            assertThat(finalBatch.getFailedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("일부 배송 처리 실패 시 나머지는 계속 처리하고 배치를 FAILED로 완료한다")
        void run_partialProcessorFailure_continuesAndMarksBatchFailed() {
            Delivery delivery1 = pendingDelivery(DELIVERY_ID_1, DeliveryType.INSPECTION_INBOUND, null);
            Delivery delivery2 = pendingDelivery(DELIVERY_ID_2, DeliveryType.ORDER_DELIVERY, UUID.randomUUID());

            given(deliveryRepository.findAllByStatus(DeliveryStatus.PENDING)).willReturn(List.of(delivery1, delivery2));
            given(deliveryBatchRepository.save(any())).willAnswer(i -> i.getArgument(0));
            willThrow(new RuntimeException("carrier 오류")).given(processor).process(any(), any(), any());

            deliveryBatchService.run();

            ArgumentCaptor<DeliveryBatch> captor = ArgumentCaptor.forClass(DeliveryBatch.class);
            then(deliveryBatchRepository).should(times(3)).save(captor.capture());

            DeliveryBatch finalBatch = captor.getValue();
            assertThat(finalBatch.getStatus()).isEqualTo(BatchStatus.FAILED);
            assertThat(finalBatch.getSuccessCount()).isEqualTo(0);
            assertThat(finalBatch.getFailedCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("배치 저장은 PREPARING → PROCESSING → COMPLETED/FAILED 순으로 3회 호출된다")
        void run_batchSaveCalledThreeTimes() {
            given(deliveryRepository.findAllByStatus(DeliveryStatus.PENDING)).willReturn(List.of());
            given(deliveryBatchRepository.save(any())).willAnswer(i -> i.getArgument(0));

            deliveryBatchService.run();

            then(deliveryBatchRepository).should(times(3)).save(any(DeliveryBatch.class));
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
