package com.chunsun.paymentservice.common.exception;

import com.chunsun.paymentservice.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode){
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
