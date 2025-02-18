package com.chunsun.chatservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chunsun.chatservice.domain.ChatMessage;
import com.chunsun.chatservice.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageSetviceImpl implements ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;

	@Override
	public List<ChatMessage> findByRoomId(String roomId) {
		List<ChatMessage> messages = chatMessageRepository.findByRoomId(roomId);
		return messages;
	}
}
