package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventJpaEntity, String> {
}
