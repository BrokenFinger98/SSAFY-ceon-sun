package com.chunsun.chatservice.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(collection = "chat_messages")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessage {
	@Id
	private String id;
	private String roomId;
	private String senderId;
	private String message;
	private LocalDateTime sentAt;
	private boolean isRead;
}
