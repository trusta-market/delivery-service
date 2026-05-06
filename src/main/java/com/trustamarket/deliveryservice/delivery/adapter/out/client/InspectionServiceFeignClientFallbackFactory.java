package com.trustamarket.deliveryservice.delivery.adapter.out.client;

import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryErrorCode;
import com.trustamarket.deliveryservice.delivery.domain.exception.DeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InspectionServiceFeignClientFallbackFactory implements FallbackFactory<InspectionServiceFeignClient> {

    @Override
    public InspectionServiceFeignClient create(Throwable cause) {
        return productId -> {
            log.error("inspection-service 호출 실패 (CB open): productId={}, cause={}", productId, cause.getMessage());
            throw new DeliveryException(DeliveryErrorCode.CENTER_NOT_FOUND,
                    "inspection-service 호출 불가: productId=" + productId);
        };
    }
}
