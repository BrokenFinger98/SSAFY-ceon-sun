package com.chunsun.chatservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chunsun.chatservice.domain.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
	List<ChatMessage> findByRoomId(String roomId);
}