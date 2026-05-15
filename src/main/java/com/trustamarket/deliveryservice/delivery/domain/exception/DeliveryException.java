package com.trustamarket.deliveryservice.delivery.domain.exception;

import com.trustamarket.common.exception.CustomException;

public class DeliveryException extends CustomException {

    private final DeliveryErrorCode errorCode;

    public DeliveryException(DeliveryErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public DeliveryException(DeliveryErrorCode errorCode, String detail) {
        super(errorCode.getStatus(), errorCode.getMessage() + ": " + detail, errorCode.getField());
        this.errorCode = errorCode;
    }

    @Override
    public String getType() {
        return errorCode.getCode();
    }

    public DeliveryErrorCode getErrorCode() {
        return errorCode;
    }
}
