package com.chunsun.chatservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chunsun.chatservice.domain.ChatMessage;

@Service
public interface ChatMessageService {
	List<ChatMessage> findByRoomId(String roomId);
}
