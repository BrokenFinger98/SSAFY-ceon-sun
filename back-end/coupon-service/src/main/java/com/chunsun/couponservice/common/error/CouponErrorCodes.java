package com.chunsun.couponservice.common.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponErrorCodes implements ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "COUPON4001", "올바르지 않은 요청입니다."),
    INVALID_AUTHORITY(FORBIDDEN, "COUPON4002", "해당 리소스에 접근할 권한이 없습니다."),
    MISSING_AUTHORIZATION_HEADER(UNAUTHORIZED, "COUPON4003", "Authorization 헤더가 누락되었습니다."),
    INVALID_BEARER_TOKEN(UNAUTHORIZED, "COUPON4004", "올바른 Bearer 토큰이 필요합니다."),
    FORBIDDEN_ACCESS(FORBIDDEN, "COUPON4005", "관리자 권한이 필요합니다."),

    MISSING_USER_ID_HEADER(BAD_REQUEST, "COUPON4006", "X-User-ID 헤더가 누락되었습니다."),
    INVALID_USER_ID_FORMAT(BAD_REQUEST, "COUPON4007", "X-User-ID 형식이 올바르지 않습니다."),

    INVALID_JWT_TOKEN(UNAUTHORIZED, "COUPON4008", "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(UNAUTHORIZED, "COUPON4009", "만료된 JWT 토큰입니다."),

    REDIS_SAVE_FAILED(INTERNAL_SERVER_ERROR, "COUPON4010", "쿠폰 생성에 실패 했습니다."),
    REDIS_ISSUE_FAILED(INTERNAL_SERVER_ERROR, "COUPON4011", "쿠폰 발급에 실패 했습니다."),

    COUPON_OUT_OF_STOCK(BAD_REQUEST, "COUPON4012", "쿠폰이 모두 소진되었습니다."),
    COUPON_ALREADY_ISSUED(BAD_REQUEST, "COUPON4013", "이미 발급받은 쿠폰입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
