package com.chunsun.notificationservice.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.chunsun.notificationservice.application.service.SseService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/sse")
public class SseController {
	private final SseService sseService;

	/**
	 * 특정 사용자(userId)에 대한 SSE 구독을 등록
	 * 구독이 성공하면 서버가 생성한 SseEmitter를 통해 향후 발생하는 알림 데이터를 실시간으로 전송받을 수 있음
	 *
	 * @param userId   구독하려는 사용자 ID
	 * @param response HTTP 응답 객체로, SSE 헤더 설정을 위해 사용
	 * @return 생성된 SseEmitter를 담은 ResponseEntity
	 */
	@GetMapping(value = "/subscribe/{userId}")
	public ResponseEntity<SseEmitter> subscribe(@PathVariable Long userId, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("X-Accel-Buffering", "no");

		var emitter = sseService.subscribe(userId);
		return ResponseEntity.ok(emitter);
	}
}
