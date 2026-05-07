package com.trustamarket.deliveryservice.delivery.adapter.in.scheduler;

import com.trustamarket.deliveryservice.delivery.application.port.in.RunDeliveryBatchUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeliveryBatchSchedulerTest {

    @Mock private RunDeliveryBatchUseCase runDeliveryBatchUseCase;

    @InjectMocks private DeliveryBatchScheduler scheduler;

    @Test
    @DisplayName("AM 배치 트리거 시 RunDeliveryBatchUseCase.run()을 호출한다")
    void runAmBatch_callsUseCase() {
        scheduler.runAmBatch();

        then(runDeliveryBatchUseCase).should().run();
    }

    @Test
    @DisplayName("PM 배치 트리거 시 RunDeliveryBatchUseCase.run()을 호출한다")
    void runPmBatch_callsUseCase() {
        scheduler.runPmBatch();

        then(runDeliveryBatchUseCase).should().run();
    }
}
