package com.trustamarket.deliveryservice.delivery.domain.enums;

public enum DeliveryType {
    INSPECTION_INBOUND,  // 판매자 → 검수센터
    INSPECTION_RETURN,   // 검수센터 → 판매자
    ORDER_DELIVERY,      // 판매자(LOW) or 검수센터(HIGH) → 구매자
    ORDER_RETURN         // 구매자 → 검수센터 (미구현)
}
