package com.chunsun.paymentservice.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCodes implements ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "PAYMENT4001", "올바르지 않은 요청입니다."),
    PAYMENT_VALIDATION_ERROR(INTERNAL_SERVER_ERROR, "PAYMENT4002", "결제 검증 중 오류가 발생했습니다."),
    PAYMENT_NOT_FOUND(BAD_REQUEST, "PAYMENT4003", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(BAD_REQUEST, "PAYMENT4004", "결제 금액이 일치하지 않습니다."),
    PAYMENT_STATUS_INVALID(BAD_REQUEST, "PAYMENT4005", "결제 상태가 올바르지 않습니다."),
    PAYMENT_CANCEL_ERROR(INTERNAL_SERVER_ERROR, "PAYMENT4006", "결제 취소에 실패했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
