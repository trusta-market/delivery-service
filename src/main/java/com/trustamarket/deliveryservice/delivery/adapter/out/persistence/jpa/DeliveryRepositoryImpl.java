package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

import com.trustamarket.deliveryservice.delivery.application.port.out.DeliveryRepository;
import com.trustamarket.deliveryservice.delivery.domain.enums.DeliveryStatus;
import com.trustamarket.deliveryservice.delivery.domain.model.Delivery;
import com.trustamarket.deliveryservice.delivery.domain.vo.DeliveryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository jpaRepository;
    private final DeliveryMapper mapper;

    @Override
    public Delivery save(Delivery delivery) {
        DeliveryJpaEntity entity = jpaRepository.findById(delivery.getId().value())
                .map(existing -> {
                    existing.update(delivery);
                    return existing;
                })
                .orElseGet(() -> DeliveryJpaEntity.of(delivery));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Delivery> findById(DeliveryId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Delivery> findAllByStatus(DeliveryStatus status) {
        return jpaRepository.findAllByStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
