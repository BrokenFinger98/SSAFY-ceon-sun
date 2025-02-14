package com.chunsun.notificationservice.application.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.chunsun.notificationservice.application.dto.NotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseService {
	@Value("${sse.subscribe.timeout}")
	private Long timeout;

	private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	public SseEmitter subscribe(Long userId) {
		SseEmitter emitter = new SseEmitter(timeout);
		sseEmitters.put(userId, emitter);

		emitter.onCompletion(() -> sseEmitters.remove(userId));
		emitter.onTimeout(() -> sseEmitters.remove(userId));
		emitter.onError(e -> sseEmitters.remove(userId));

		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data(true));
		} catch (IOException e) {
			log.error("SSE 초기 연결 실패: {}", e.getMessage());
		}

		return emitter;
	}

	public void sendNotification(String userId, String notificationId) {
		SseEmitter emitter = sseEmitters.get(Long.valueOf(userId));
		if (emitter == null) {
			log.warn("SSE Emitter 없음 : {}", userId);
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.name("notification")
				.data(notificationId));
			log.info("SSE 전송 성공 id : {}", notificationId);
		} catch (IOException e) {
			sseEmitters.remove(userId);
			log.error("SSE 전송 실패: {}", e.getMessage());
		}
	}
}
