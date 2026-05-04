package com.trustamarket.deliveryservice.delivery.domain.exception;

import com.trustamarket.common.exception.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum DeliveryErrorCode implements ErrorCodeSpec {

    DELIVERY_NOT_FOUND("DL-001", HttpStatus.NOT_FOUND, "배송 정보를 찾을 수 없습니다", ""),
    INVALID_STATUS_TRANSITION("DL-002", HttpStatus.BAD_REQUEST, "현재 상태에서 허용되지 않는 작업입니다", "status"),
    DELIVERY_ALREADY_SHIPPED("DL-003", HttpStatus.BAD_REQUEST, "이미 배송 요청된 배송입니다", "status"),
    INVALID_DELIVERY_ID("DL-004", HttpStatus.BAD_REQUEST, "DeliveryId는 null일 수 없습니다", ""),
    INVALID_DELIVERY_BATCH_ID("DL-005", HttpStatus.BAD_REQUEST, "DeliveryBatchId는 null일 수 없습니다", ""),
    INVALID_ORDER_ID("DL-006", HttpStatus.BAD_REQUEST, "OrderId는 null일 수 없습니다", ""),
    INVALID_PRODUCT_ID("DL-007", HttpStatus.BAD_REQUEST, "ProductId는 null일 수 없습니다", ""),
    INVALID_SENDER_ID("DL-008", HttpStatus.BAD_REQUEST, "SenderId는 null일 수 없습니다", ""),
    INVALID_RECEIVER_ID("DL-009", HttpStatus.BAD_REQUEST, "ReceiverId는 null일 수 없습니다", "");

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final String field;

    DeliveryErrorCode(String code, HttpStatus status, String message, String field) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.field = field;
    }

    @Override public String getCode()       { return code; }
    @Override public HttpStatus getStatus() { return status; }
    @Override public String getMessage()    { return message; }
    @Override public String getField()      { return field; }
}
