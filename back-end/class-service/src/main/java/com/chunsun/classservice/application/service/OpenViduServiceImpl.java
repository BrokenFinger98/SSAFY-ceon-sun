package com.chunsun.classservice.application.service;

import static com.chunsun.classservice.common.error.ClassErrorCodes.CONTRACTED_CLASS_NOT_FOUND;
import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_AUTHORITY;
import static com.chunsun.classservice.common.error.ClassErrorCodes.INVALID_REQUEST;
import static com.chunsun.classservice.common.error.ClassErrorCodes.OPENVIDU_SESSION_CLOSE_ERROR;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.classservice.application.client.MemberClient;
import com.chunsun.classservice.application.client.RankClient;
import com.chunsun.classservice.application.dto.FeignDto.ClassFinishRequest;
import com.chunsun.classservice.common.exception.BusinessException;
import com.chunsun.classservice.domain.ContractedClass;
import com.chunsun.classservice.domain.ContractedClassRepository;
import com.chunsun.classservice.domain.LessonRecord;
import com.chunsun.classservice.domain.LessonRecordRepository;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.MediaMode;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OpenViduServiceImpl implements OpenViduService {

	private final MemberClient memberClient;
	private final RankClient rankClient;
	private final LessonRecordRepository lessonRecordRepository;
	private final ContractedClassRepository contractedClassRepository;
	private final OpenVidu openVidu;

	@Override
	public Connection getSession(final Long memberId, final Long contractedClassId)
		throws OpenViduJavaClientException, OpenViduHttpException {

		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));

		if (!contractedClass.getStudentId().equals(memberId) && !contractedClass.getTeacherId().equals(memberId)) {
			throw new BusinessException(INVALID_AUTHORITY);
		}

		final String role = contractedClass.getTeacherId().equals(memberId) ? "teacher" : "student";

		updateJoinTime(contractedClass, role);

		final Session session = getOrCreateSession(contractedClassId);
		log.info("Session created: {}", session);

		Connection connection = session.createConnection(getConnectionProperties());
		log.info("Token issued: {}", connection.getToken());

		return connection;
	}

	@Override
	public void deleteSession(Long memberId, Long contractedClassId) {
		final ContractedClass contractedClass = contractedClassRepository.findById(contractedClassId)
			.orElseThrow(() -> new BusinessException(CONTRACTED_CLASS_NOT_FOUND));

		if (!contractedClass.getStudentId().equals(memberId) && !contractedClass.getTeacherId().equals(memberId)) {
			throw new BusinessException(INVALID_AUTHORITY);
		}

		final String role = contractedClass.getTeacherId().equals(memberId) ? "teacher" : "student";

		final LessonRecord lessonRecord = updateExitTime(contractedClass, role);

		final Session session = openVidu.getActiveSession(contractedClassId.toString());
		if (session != null) {
			try {
				session.close();
			} catch (OpenViduJavaClientException | OpenViduHttpException e) {
				throw new BusinessException(OPENVIDU_SESSION_CLOSE_ERROR);
			}
		} else {
			lessonRecord.calculateLessonTime();
			contractedClass.decreaseRemainClass();
			int lessonMinutes = (int) ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, lessonRecord.getLessonTime());
			memberClient.classCountUpdate(contractedClass.getTeacherId(), new ClassFinishRequest(lessonMinutes));
			rankClient.incrementTeacherClassCount(contractedClass.getTeacherId());
		}
	}

	private void updateJoinTime(final ContractedClass contractedClass, final String role) {
		if ("student".equals(role)) {
			LessonRecord lessonRecord = lessonRecordRepository
				.findByContractedClassAndStudentJoinTimeIsNull(contractedClass)
				.orElse(new LessonRecord(contractedClass));
			lessonRecord.updateStudentJoinTime();
			lessonRecordRepository.save(lessonRecord);
		} else {
			LessonRecord lessonRecord = lessonRecordRepository
				.findByContractedClassAndTeacherJoinTimeIsNull(contractedClass)
				.orElse(new LessonRecord(contractedClass));
			lessonRecord.updateTeacherJoinTime();
			lessonRecordRepository.save(lessonRecord);
		}
	}

	private LessonRecord updateExitTime(final ContractedClass contractedClass, final String role) {
		final LessonRecord lessonRecord;
		if ("student".equals(role)) {
			lessonRecord = lessonRecordRepository.findByContractedClassAndStudentExitTimeIsNull(contractedClass)
				.orElseThrow(() -> new BusinessException(INVALID_REQUEST));
			lessonRecord.updateStudentExitTime();
		} else {
			lessonRecord = lessonRecordRepository.findByContractedClassAndTeacherExitTimeIsNull(contractedClass)
				.orElseThrow(() -> new BusinessException(INVALID_REQUEST));
			lessonRecord.updateTeacherExitTime();
		}
		lessonRecordRepository.save(lessonRecord);
		return lessonRecord;
	}

	private Session getOrCreateSession(final Long contractedClassId)
		throws OpenViduJavaClientException, OpenViduHttpException {

		Session session = openVidu.getActiveSession(contractedClassId.toString());
		if (session == null) {
			SessionProperties properties = new SessionProperties.Builder()
				.customSessionId(contractedClassId.toString())
				.mediaMode(MediaMode.ROUTED)
				.build();
			session = openVidu.createSession(properties);
		}
		return session;
	}

	private ConnectionProperties getConnectionProperties() {
		return new ConnectionProperties.Builder()
			.role(OpenViduRole.PUBLISHER)
			.build();
	}
}