package com.chunsun.chatservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chunsun.chatservice.config.feign.MemberClient;
import com.chunsun.chatservice.converter.ChatRoomConverter;
import com.chunsun.chatservice.domain.ChatRoom;
import com.chunsun.chatservice.repository.ChatRoomRepository;
import com.chunsun.chatservice.web.dto.ChatRoomDto;
import com.chunsun.chatservice.web.dto.MemberInfoDto;
import com.chunsun.chatservice.web.dto.MessageDto;
import com.chunsun.chatservice.web.dto.NewChatRoomDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

	private final MessageService messageService;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberClient memberClient;

	@Override
	public void requestClass(NewChatRoomDto.RequestDto requestDto) {
		// 1. 회원 검증
		// TODO: 존재하는 회원인지 확인

		//  2-1. 채팅방이 존재하지 않으면 채팅방 생성
		ChatRoom chatRoom;
		Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByStudentIdAndTeacherId(requestDto.studentId(),
			requestDto.teacherId());
		if (chatRoomOptional.isEmpty()) {
			log.info("채팅방이 존재하지 않아 채팅방을 생성합니다.");
			log.info("studentId: {}, teacherId: {}", requestDto.studentId(),
				requestDto.teacherId());
			// 채팅방 생성
			chatRoom = chatRoomRepository.save(ChatRoomConverter.ToEntity(requestDto));
		} else {
			chatRoom = chatRoomOptional.get();
		}

		// 2-2. 초기 문의 메시지 전송
		MessageDto initMessage = MessageDto.builder()
			.roomId(chatRoom.getId().toString())
			.senderId(requestDto.studentId().toString())
			.message("(초기메시지) 안녕하세요! 과외 문의 드려요~")
			.sentAt(LocalDateTime.now().toString())
			.build();

		// 메시지 전송
		messageService.sendMessage(initMessage);
	}

	@Override
	public List<ChatRoomDto.ResponseDto> getChatRoomsByUserId(String userId, String role) {
		Long id = Long.parseLong(userId);
		List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserId(id);

		// 상대방 ID 수집
		List<Long> partnerIds = chatRooms.stream()
			.map(chatRoom -> role.equals("GUEST") ? chatRoom.getTeacherId() : chatRoom.getStudentId())
			.distinct()
			.toList();

		log.info("partnerIds: {}", partnerIds);

		if (partnerIds.isEmpty()) {
			return List.of();
		}

		// Member 서비스에 요청하여 이름 및 프로필 이미지 가져오기
		List<MemberInfoDto.ResponseDto> membersInfo = memberClient.getMembersNickname(partnerIds);

		// 결과 변환 및 ResponseDto 생성
		return chatRooms.stream().map(chatRoom -> {
			Long partnerId = role.equals("STUDENT") ? chatRoom.getTeacherId() : chatRoom.getStudentId();

			// 리스트에서 partnerId에 해당하는 정보 찾기
			MemberInfoDto.ResponseDto partnerInfo = membersInfo.stream()
				.filter(member -> member.id().equals(partnerId))
				.findFirst()
				.orElse(null);

			String partnerName = (partnerInfo != null) ? partnerInfo.nickname() : "알 수 없음";
			String profileImage =
				(partnerInfo != null) ? partnerInfo.profileImage() :
					"https://cdn.pixabay.com/photo/2021/07/02/04/48/user-6380868_1280.png";

			return ChatRoomDto.ResponseDto.builder()
				.id(chatRoom.getId())
				.partnerId(partnerId)
				.partnerName(partnerName)
				.profileImage(profileImage)
				.build();
		}).collect(Collectors.toList());
	}
}
