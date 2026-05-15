package com.trustamarket.deliveryservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.trustamarket.deliveryservice.delivery.adapter.out.client")
public class FeignClientConfig {
}
