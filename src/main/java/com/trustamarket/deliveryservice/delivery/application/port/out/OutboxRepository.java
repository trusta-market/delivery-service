package com.trustamarket.deliveryservice.delivery.application.port.out;

public interface OutboxRepository {

    void save(String topic, String messageKey, String payload);
}
