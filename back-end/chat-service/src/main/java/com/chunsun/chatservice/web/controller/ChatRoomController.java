package com.chunsun.chatservice.web.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.chatservice.kafka.ChatMessageProducer;
import com.chunsun.chatservice.service.ChatRoomService;
import com.chunsun.chatservice.util.JwtUtil;
import com.chunsun.chatservice.web.dto.ChatRoomDto;
import com.chunsun.chatservice.web.dto.MessageDto;
import com.chunsun.chatservice.web.dto.NewChatRoomDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final ChatMessageProducer chatMessageProducer;
	private final JwtUtil jwtUtil;

	@GetMapping("/chat")
	public String chat() {
		String now = LocalDateTime.now(ZoneOffset.UTC) // 현재 UTC 시간
			.format(DateTimeFormatter.ISO_INSTANT);
		chatMessageProducer.send(MessageDto.builder()
			.roomId("1")
			.senderId("1")
			.message("Hello, world!")
			.sentAt(now)
			.build());
		return "chat";
	}

	@GetMapping("/")
	public String testController() {
		return "chat-service";
	}

	/**
	 * 괴외 문의하기 클릭시
	 * 1. 채팅방 존재 유무 확인
	 * 2-1 채팅방이 존재하면 초기 문의 메시지 전송
	 * 2-2 채팅방이 존재하지 않으면 채팅방 생성 + 초기 문의 메시지 전송
	 */
	@PostMapping("/chat-rooms")
	public ResponseEntity<String> newChatRoom(@RequestBody NewChatRoomDto.RequestDto requestDto) {
		chatRoomService.requestClass(requestDto);
		return ResponseEntity.ok().body("문의 메시지 전송 완료");
	}

	/**
	 * 내 채팅방 목록 조회
	 * 채팅방 ID, 상대방 ID, 상대방 이름
	 */
	@GetMapping("/chat-rooms")
	public ResponseEntity<List<ChatRoomDto.ResponseDto>> getChatRooms(
		@RequestHeader("Authorization") String token) {
		token = token.substring(7);
		log.info("token: {}", token);

		String userId = jwtUtil.getId(token);
		String role = jwtUtil.getRole(token);

		log.info("userId: {}, role: {}", userId, role);

		List<ChatRoomDto.ResponseDto> chatRoomsByUserId = chatRoomService.getChatRoomsByUserId(userId, role);
		return ResponseEntity.ok().body(chatRoomsByUserId);
	}
}
