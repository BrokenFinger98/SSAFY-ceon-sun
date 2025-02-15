package com.chunsun.chatservice.service;

import java.util.List;

import com.chunsun.chatservice.web.dto.ChatRoomDto;
import com.chunsun.chatservice.web.dto.NewChatRoomDto;

public interface ChatRoomService {
	void requestClass(NewChatRoomDto.RequestDto requestDto);

	List<ChatRoomDto.ResponseDto> getChatRoomsByUserId(String userId, String role);
}
