package com.chunsun.gatewayservice.util;

import org.apache.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public class HeaderUtil {
	private static final String BEARER_PREFIX = "Bearer ";

	public static String getToken(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		return extractToken(authHeader);
	}

	public static String extractToken(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			return null;
		}

		if (authorizationHeader.startsWith(BEARER_PREFIX)) {
			return authorizationHeader.substring(BEARER_PREFIX.length());
		}

		return authorizationHeader;
	}
}

