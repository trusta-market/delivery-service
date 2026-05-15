package com.trustamarket.deliveryservice.delivery.application.port.out;

public interface ProcessedEventRepository {
    boolean existsByEventKey(String eventKey);
    void save(String eventKey);
}
