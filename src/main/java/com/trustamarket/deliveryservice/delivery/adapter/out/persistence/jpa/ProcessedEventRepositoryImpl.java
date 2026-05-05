package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.application.port.out.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryImpl implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository jpaRepository;

    @Override
    public boolean existsByEventKey(String eventKey) {
        return jpaRepository.existsById(eventKey);
    }

    @Override
    public void save(String eventKey) {
        jpaRepository.save(ProcessedEventJpaEntity.of(eventKey));
    }
}
