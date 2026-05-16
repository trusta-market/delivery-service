package com.trustamarket.deliveryservice.delivery.adapter.out.persistence.jpa;

public enum OutboxStatus {
    PENDING,
    IN_PROGRESS,
    PUBLISHED,
    FAILED
}
