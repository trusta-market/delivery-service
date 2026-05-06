package com.trustamarket.deliveryservice.delivery.adapter.out.client;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Retryer retryer() {
        // 100ms 초기 대기, 최대 1s, 최대 3회 시도
        return new Retryer.Default(100, 1_000, 3);
    }
}
