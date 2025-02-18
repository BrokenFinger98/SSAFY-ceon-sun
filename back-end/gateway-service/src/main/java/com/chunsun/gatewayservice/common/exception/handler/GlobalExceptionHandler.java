package com.chunsun.gatewayservice.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chunsun.gatewayservice.common.error.ErrorCode;
import com.chunsun.gatewayservice.common.error.ErrorResponseDto;
import com.chunsun.gatewayservice.common.error.GlobalErrorCodes;
import com.chunsun.gatewayservice.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e) {
		log.error("BusinessException : {}", e.getErrorCode().getMessage());
		ErrorCode errorMessage = e.getErrorCode();
		return ErrorResponseDto.of(errorMessage);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponseDto> handleException(Exception e) {
		log.error("Exception : {}", e.getMessage());
		return ErrorResponseDto.of(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
	}
}
