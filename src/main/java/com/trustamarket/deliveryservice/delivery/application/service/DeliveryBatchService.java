package com.trustamarket.deliveryservice.delivery.application.service;

import com.trustamarket.deliveryservice.delivery.application.port.in.RunDeliveryBatchUseCase;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryBatchRepository;
import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.model.DeliveryBatch;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryBatchId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryBatchService implements RunDeliveryBatchUseCase {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryBatchRepository deliveryBatchRepository;
    private final DeliveryBatchProcessor processor;

    @Override
    public void run() {
        Instant now = Instant.now();

        DeliveryBatch batch = DeliveryBatch.prepare(DeliveryBatchId.generate(), now);
        deliveryBatchRepository.save(batch);

        List<Delivery> pending = deliveryRepository.findAllByStatus(DeliveryStatus.PENDING);
        batch.startProcessing(pending.size());
        deliveryBatchRepository.save(batch);

        log.info("배송 배치 PROCESSING: batchId={}, totalCount={}", batch.getId().value(), pending.size());

        int success = 0;
        int failed = 0;

        for (Delivery delivery : pending) {
            try {
                processor.process(delivery, batch.getId(), now);
                success++;
            } catch (Exception e) {
                log.error("배송 배치 처리 실패: deliveryId={}", delivery.getId().value(), e);
                failed++;
            }
        }

        batch.complete(success, failed, Instant.now());
        deliveryBatchRepository.save(batch);

        log.info("배송 배치 완료: batchId={}, success={}, failed={}", batch.getId().value(), success, failed);
    }
}
