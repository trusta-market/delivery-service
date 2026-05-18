package com.trustamarket.deliveryservice.delivery.adapter.out.client;

import com.trustamarket.deliveryservice.delivery.application.port.out.InspectionCenterClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InspectionCenterClientAdapter implements InspectionCenterClient {

    private final InspectionServiceFeignClient feignClient;

    @Override
    public Optional<UUID> getCenterIdByProductId(UUID productId) {
        try {
            return Optional.ofNullable(feignClient.getCenterIdByProductId(productId).data().centerId());
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }
}
