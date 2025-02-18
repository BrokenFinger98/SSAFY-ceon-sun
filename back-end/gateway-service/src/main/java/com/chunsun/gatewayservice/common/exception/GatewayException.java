package com.chunsun.gatewayservice.common.exception;

import com.chunsun.gatewayservice.common.error.ErrorCode;

public class GatewayException extends BusinessException {
	public GatewayException(ErrorCode errorCode) {
		super(errorCode);
	}
}
