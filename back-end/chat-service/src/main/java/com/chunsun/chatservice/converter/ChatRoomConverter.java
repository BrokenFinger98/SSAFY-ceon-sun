package com.chunsun.chatservice.converter;

import com.chunsun.chatservice.domain.ChatRoom;
import com.chunsun.chatservice.web.dto.NewChatRoomDto;

public class ChatRoomConverter {
	public static ChatRoom ToEntity(NewChatRoomDto.RequestDto requestDto) {
		return ChatRoom.builder()
			.studentId(requestDto.studentId())
			.teacherId(requestDto.teacherId())
			.build();
	}

	// public static NewChatRoomDto.ResponseDto ToDto(ChatRoom chatRoom) {
	// 	return
	// }
}
