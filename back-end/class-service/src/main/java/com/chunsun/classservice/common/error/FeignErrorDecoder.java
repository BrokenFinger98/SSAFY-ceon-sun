package com.chunsun.classservice.common.error;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.chunsun.classservice.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			FeignErrorResponse errorResponse = objectMapper.readValue(
				response.body().asInputStream(),
				FeignErrorResponse.class
			);

			// JSON 응답에서 ErrorCode 생성
			ErrorCode errorCode = createErrorCode(errorResponse, response.status());

			return new BusinessException(errorCode);
		} catch (IOException e) {
			return new BusinessException(GlobalErrorCodes.INVALID_JSON_DATA);
		}
	}

	private ErrorCode createErrorCode(FeignErrorResponse errorResponse, int status) {
		return new ErrorCode() {
			@Override
			public HttpStatus getHttpStatus() {
				return HttpStatus.valueOf(status);
			}

			@Override
			public String getCode() {
				return errorResponse.getCode();
			}

			@Override
			public String getMessage() {
				return errorResponse.getMessage();
			}
		};
	}

	@Getter
	@Setter
	public static class FeignErrorResponse {
		private String code;
		private String message;
	}
}
