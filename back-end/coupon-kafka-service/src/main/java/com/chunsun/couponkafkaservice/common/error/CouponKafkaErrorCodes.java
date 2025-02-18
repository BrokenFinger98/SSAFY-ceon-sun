package com.chunsun.couponkafkaservice.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponKafkaErrorCodes implements ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "COUPONKAFKA4001", "올바르지 않은 요청입니다."),
    INVALID_COUPON_DELETE_REQUEST(BAD_REQUEST, "COUPONKAFKA4002", "존재하지 않는 쿠폰입니다."),
    INVALID_COUPON_STATUS_UPDATE_REQUEST(BAD_REQUEST, "COUPONKAFKA4003", "존재하지 않는 Memeber_Conpon입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
