package com.chunsun.paymentservice.config.interceptor;

import static com.chunsun.paymentservice.common.error.GlobalErrorCodes.INVALID_HEADER_DATA;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.chunsun.paymentservice.common.exception.BusinessException;
import com.chunsun.paymentservice.common.resolver.UserId;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor  implements HandlerInterceptor {

	private static final String USER_ID_HEADER = "X-User-ID";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			UserId userIdAnnotation = handlerMethod.getMethodAnnotation(UserId.class);

			if (userIdAnnotation == null) {
				return true;
			}

			String userIdHeader  = request.getHeader(USER_ID_HEADER);

			try {
				Long userId = Long.parseLong(userIdHeader);
				request.setAttribute("UserId", userId);

				return true;
			} catch (RuntimeException e) {
				throw new BusinessException(INVALID_HEADER_DATA);
			}
		}
		return true;
	}
}
