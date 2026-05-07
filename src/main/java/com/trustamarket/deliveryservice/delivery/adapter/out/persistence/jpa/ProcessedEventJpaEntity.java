package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "p_processed_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProcessedEventJpaEntity {

    @Id
    @Column(unique = true)
    private String eventKey;

    @Column(nullable = false)
    private Instant processedAt;

    public static ProcessedEventJpaEntity of(String eventKey) {
        ProcessedEventJpaEntity e = new ProcessedEventJpaEntity();
        e.eventKey = eventKey;
        e.processedAt = Instant.now();
        return e;
    }
}
