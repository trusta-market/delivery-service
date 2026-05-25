package com.trustamarket.deliveryservice.delivery.adapter.in.web;

import com.trustamarket.common.response.CommonResponse;
import com.trustamarket.deliveryservice.delivery.adapter.in.web.dto.response.GetDeliveryStatusResponse;
import com.trustamarket.deliveryservice.delivery.application.port.in.GetDeliveryStatusUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryApiController {

    private final GetDeliveryStatusUseCase getDeliveryStatusUseCase;

    @GetMapping("/{deliveryId}/status")
    public ResponseEntity<CommonResponse<GetDeliveryStatusResponse>> getDeliveryStatus(@PathVariable UUID deliveryId) {
        return ResponseEntity.ok(CommonResponse.of(
                HttpStatus.OK.value(),
                GetDeliveryStatusResponse.from(getDeliveryStatusUseCase.get(deliveryId)))
        );
    }
}
