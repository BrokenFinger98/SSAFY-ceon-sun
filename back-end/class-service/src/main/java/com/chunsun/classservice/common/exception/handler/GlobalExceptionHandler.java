package com.chunsun.classservice.common.exception.handler;

import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_REQUEST;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.chunsun.classservice.common.error.ErrorCode;
import com.chunsun.classservice.common.error.ErrorResponseDto;
import com.chunsun.classservice.common.error.GlobalErrorCodes;
import com.chunsun.classservice.common.exception.BusinessException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		final MethodArgumentNotValidException e,
		final HttpHeaders headers,
		final HttpStatusCode status,
		final WebRequest request
	) {
		log.warn(e.getMessage(), e);
		return ResponseEntity.badRequest().body(new ErrorResponseDto(INVALID_REQUEST.getCode(),
			e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
			LocalDateTime.now()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(final ConstraintViolationException e) {
		log.warn(e.getMessage(), e);
		final String errorMessage = e.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("메시지 추출 도중 에러 발생"));
		return ResponseEntity.badRequest().body(new ErrorResponseDto(INVALID_REQUEST.getCode(), errorMessage,
			LocalDateTime.now()));
	}

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ErrorResponseDto> handleBusinessException(final BusinessException e) {
		log.warn(e.getMessage(), e);
		final ErrorCode errorMessage = e.getErrorCode();
		return ErrorResponseDto.of(errorMessage);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponseDto> handleException(final Exception e) {
		log.error(e.getMessage(), e);
		return ErrorResponseDto.of(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
	}
}
