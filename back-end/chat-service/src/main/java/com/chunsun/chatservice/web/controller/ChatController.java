package com.chunsun.chatservice.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.chunsun.chatservice.domain.ChatMessage;
import com.chunsun.chatservice.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final ChatMessageRepository chatMessageRepository;

	@GetMapping("/chat-rooms/{chatRoomId}")
	public ResponseEntity<List<ChatMessage>> getChatMessage(@PathVariable String chatRoomId) {
		log.info("Get chat messages by roomId: {}", chatRoomId);
		// MongoDB에서 roomId에 해당하는 메시지 조회
		List<ChatMessage> messages = chatMessageRepository.findByRoomId(chatRoomId);
		log.info("Found {} messages", messages.size());

		return ResponseEntity.ok(messages);
	}
}
