package com.trustamarket.deliveryservice.delivery.adapter.in.scheduler;

import com.trustamarket.deliveryservice.delivery.application.port.in.RunDeliveryBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryBatchScheduler {

    private final RunDeliveryBatchUseCase runDeliveryBatchUseCase;

    @Scheduled(cron = "${delivery.batch.am-cron}")
    public void runAmBatch() {
        log.info("AM 배송 배치 시작");
        runDeliveryBatchUseCase.run();
    }

    @Scheduled(cron = "${delivery.batch.pm-cron}")
    public void runPmBatch() {
        log.info("PM 배송 배치 시작");
        runDeliveryBatchUseCase.run();
    }
}
