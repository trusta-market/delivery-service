package com.trustamarket.deliveryservice.delivery.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "inspection-service",
        path = "/internal/v1/inspections",
        configuration = FeignConfig.class,
        fallbackFactory = InspectionServiceFeignClientFallbackFactory.class
)
public interface InspectionServiceFeignClient {

    @GetMapping("/center-id")
    GetCenterIdResponse getCenterIdByProductId(@RequestParam UUID productId);
}
