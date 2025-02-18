package com.chunsun.couponkafkaservice.config.interceptor;

import static com.chunsun.couponkafkaservice.common.error.GlobalErrorCodes.INVALID_HEADER_DATA;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.chunsun.couponkafkaservice.common.exception.BusinessException;
import com.chunsun.couponkafkaservice.common.resolver.UserId;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor  implements HandlerInterceptor {

	private static final String USER_ID_HEADER = "X-User-ID";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod) {
			final HandlerMethod handlerMethod = (HandlerMethod) handler;

			final UserId userIdAnnotation = handlerMethod.getMethodAnnotation(UserId.class);

			if (userIdAnnotation == null) {
				return true;
			}

			final String userIdHeader  = request.getHeader(USER_ID_HEADER);

			try {
				final Long userId = Long.parseLong(userIdHeader);
				request.setAttribute("UserId", userId);

				return true;
			} catch (RuntimeException e) {
				throw new BusinessException(INVALID_HEADER_DATA);
			}
		}
		return true;
	}
}
