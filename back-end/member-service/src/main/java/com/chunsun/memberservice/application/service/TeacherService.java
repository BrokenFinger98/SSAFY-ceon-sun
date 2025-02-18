package com.chunsun.memberservice.application.service;

import com.chunsun.memberservice.application.dto.StudentDto;
import com.chunsun.memberservice.application.dto.TeacherDto;

public interface TeacherService {

	TeacherDto.CreateCardResponse createCard(Long memberId, TeacherDto.CreateCardRequest request);

	TeacherDto.UpdateCardResponse updateCard(Long id, TeacherDto.UpdateCardRequest request);

	TeacherDto.GetCardResponse getCard(Long id);

	TeacherDto.GetDetailResponse getDetail(Long id);

	TeacherDto.ClassFinishResponse updateClass(Long id, TeacherDto.ClassFinishRequest request);

	TeacherDto.ClassFinishResponse getClass(Long id);
}
