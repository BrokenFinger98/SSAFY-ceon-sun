package com.chunsun.chatservice.socket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.chunsun.chatservice.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) {

		log.info("=== WebSocketHandshakeInterceptor.beforeHandshake ===");

		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest httpRequest = servletRequest.getServletRequest();

			// String token = httpRequest.getHeader("Authorization");
			String token = httpRequest.getParameter("token");
			log.info("Extracted token: {}", token);

			if (token != null && token.startsWith("Bearer ")) {
				String userId = jwtUtil.getId(token.substring(7)); // 토큰에서 userId 추출
				attributes.put("userId", userId); // WebSocket 세션에 userId 저장
				return true;
			}
			// 테스트용 코드
			// String userId = token.substring(7);
			// attributes.put("userId", userId); //  WebSocket 세션에 userId 저장
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Exception exception) {
		// 핸드셰이크 이후 추가 처리 필요시 구현
	}
}