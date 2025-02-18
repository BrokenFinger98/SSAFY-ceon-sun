package com.chunsun.memberservice.application.service;

import com.chunsun.memberservice.application.dto.StudentDto;

public interface StudentService {
	StudentDto.CardResponse createCard(Long id, StudentDto.CreateCardRequest request);

	StudentDto.CardResponse updateCard(Long id, StudentDto.UpdateCardRequest request);

	StudentDto.GetCardResponse getCard(Long id);

	StudentDto.GetDetailResponse getDetail(Long id);

	StudentDto.GetListResponse getList();
}
