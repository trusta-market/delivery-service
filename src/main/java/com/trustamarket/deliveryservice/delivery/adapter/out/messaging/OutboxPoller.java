package com.trustamarket.deliveryservice.delivery.adapter.out.messaging;

import com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa.DeliveryOutboxJpaEntity;
import com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa.DeliveryOutboxJpaRepository;
import com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa.OutboxStatus;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final DeliveryOutboxJpaRepository outboxJpaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxClaimAdapter outboxClaimAdapter;
    private final MeterRegistry meterRegistry;

    @Value("${delivery.outbox.max-retries:3}")
    private int maxRetries;

    @Value("${delivery.outbox.batch-size:500}")
    private int batchSize;

    @PostConstruct
    void registerMetrics() {
        Gauge.builder("delivery.outbox.pending.depth",
                () -> (double) outboxJpaRepository.countByStatus(OutboxStatus.PENDING))
            .register(meterRegistry);
    }

    @Scheduled(cron = "${delivery.outbox.scheduler-cron}")
    public void poll() {
        List<DeliveryOutboxJpaEntity> claimed = outboxClaimAdapter.claim(batchSize);
        if (claimed.isEmpty()) {
            return;
        }

        for (DeliveryOutboxJpaEntity entry : claimed) {
            try {
                kafkaTemplate.send(entry.getTopic(), entry.getMessageKey(), entry.getPayload())
                        .get(5, TimeUnit.SECONDS);
                entry.markPublished();
                log.info("outbox 발행 완료: id={}, topic={}", entry.getId(), entry.getTopic());
            } catch (Exception e) {
                log.error("outbox 발행 실패: id={}, topic={}", entry.getId(), entry.getTopic(), e);
                entry.recordFailure(e.getMessage(), maxRetries);
            }
            outboxJpaRepository.save(entry);
        }
    }
}
