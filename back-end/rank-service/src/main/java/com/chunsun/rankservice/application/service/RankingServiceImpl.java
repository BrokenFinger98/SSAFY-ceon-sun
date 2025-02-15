package com.chunsun.rankservice.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.rankservice.application.calculator.ScoreCalculator;
import com.chunsun.rankservice.application.calculator.ScoreFactors;
import com.chunsun.rankservice.common.error.GlobalErrorCodes;
import com.chunsun.rankservice.common.exception.BusinessException;
import com.chunsun.rankservice.domain.entity.RankingData;
import com.chunsun.rankservice.domain.repository.RankingRepository;

import io.lettuce.core.RedisCommandTimeoutException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingServiceImpl implements RankingService {

	private final RankingRepository rankingRepository;
	private final ZSetOperations<String, String> zSet;
	private final ScoreCalculator scoreCalculator;

	private static final String VIEW_KEY = "teacher:view"; // 조회수
	private static final String CLASS_KEY = "teacher:class"; // 수업 횟수
	private static final String SCORE_KEY = "teacher:score";   // 총 점수
	private final RedisTemplate<String, String> redisTemplate;

	// 조회수 증가
	@Override
	@Transactional
	public void incrementTeacherViewCount(Long teacherId) {
		try {
			zSet.incrementScore(VIEW_KEY, String.valueOf(teacherId), 1);
		} catch(RedisConnectionFailureException e) {
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		}
	}

	// 수업 횟수 증가
	@Override
	@Transactional
	public void incrementTeacherClassCount(Long teacherId) {
		try {
			zSet.incrementScore(CLASS_KEY, String.valueOf(teacherId), 1);
		} catch(RedisConnectionFailureException e) {
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		}
	}

	// 실시간 조회수, 수업 횟수 -> Redis 총점수 반영
	@Override
	@Transactional
	public void mergeRealTimeData() {
		// Redis에서 실시간 조회수와 수업 횟수를 가져와 로컬 변수에 저장
		Set<ZSetOperations.TypedTuple<String>> viewScores = zSet.reverseRangeWithScores(VIEW_KEY, 0, -1);
		Set<ZSetOperations.TypedTuple<String>> classScores = zSet.reverseRangeWithScores(CLASS_KEY, 0, -1);

		Set<String> allTeacherIds = new HashSet<>();

		// 모든 teacherId를 포함하는 Set 생성
		viewScores.forEach(viewData -> allTeacherIds.add(viewData.getValue()));
		classScores.forEach(classData -> allTeacherIds.add(classData.getValue()));

		// 파이프라인으로 총점 업데이트 및 Redis 데이터 삭제
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (String teacherId : allTeacherIds) {
				try {
					// viewScore 가져오기 (없으면 0)
					double viewScore = viewScores != null
						? viewScores.stream()
						.filter(viewData -> viewData.getValue().equals(teacherId))
						.mapToDouble(viewData -> viewData.getScore() != null ? viewData.getScore() : 0)
						.findFirst()
						.orElse(0)
						: 0;

					// classScore 가져오기 (없으면 0)
					double classScore = classScores != null
						? classScores.stream()
						.filter(classData -> classData.getValue().equals(teacherId))
						.mapToDouble(classData -> classData.getScore() != null ? classData.getScore() : 0)
						.findFirst()
						.orElse(0)
						: 0;

					// ScoreFactors 설정 및 가중치 적용 점수 계산
					ScoreFactors factors = new ScoreFactors();
					factors.setViewScore(viewScore);
					factors.setClassScore(classScore);
					double weightedScore = scoreCalculator.calculateScore(factors);

					// 기존 총점수 조회 후 업데이트
					Double score = zSet.score(SCORE_KEY, teacherId);
					double previousScore = (score != null) ? score : 0;

					connection.zAdd(SCORE_KEY.getBytes(), previousScore + weightedScore, teacherId.getBytes());

					// 실시간 데이터 삭제
					connection.zRem(VIEW_KEY.getBytes(), teacherId.getBytes());
					connection.zRem(CLASS_KEY.getBytes(), teacherId.getBytes());
				} catch (Exception e) {
					throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
				}
			}
			return null;
		});

		List<RankingData> rankingDataList = new ArrayList<>();

		// viewScores를 기반으로 DB 업데이트 수행
		for (ZSetOperations.TypedTuple<String> viewData : viewScores) {
			String teacherId = viewData.getValue();
			double viewScore = (viewData.getScore() != null) ? viewData.getScore() : 0;

			// classScores에서도 해당 teacherId의 수업 횟수를 찾음
			double classScore = classScores.stream()
				.filter(classData -> classData.getValue().equals(teacherId))
				.mapToDouble(classData -> classData.getScore() != null ? classData.getScore() : 0)
				.findFirst()
				.orElse(0);

			// 기존 RankingData 조회 또는 새 객체 생성
			RankingData rankingData;

			try {
				rankingData = rankingRepository.findById(Long.parseLong(teacherId))
					.orElseGet(() -> new RankingData(Long.parseLong(teacherId), 0, 0, 0L));
			} catch (NumberFormatException e) {
				// 사용자 예외처리: teacherId 형식 문제 (예: BAD_REQUEST)
				throw new BusinessException(GlobalErrorCodes.NOT_FOUND_URL);
			} catch (DataAccessException e) {
				// 사용자 예외처리: DB 접근 문제 (예: INTERNAL_SERVER_ERROR)
				throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
			}

			rankingData.setTotalViews(rankingData.getTotalViews() + (int) viewScore);
			rankingData.setTotalLessons(rankingData.getTotalLessons() + (int) classScore);
			rankingDataList.add(rankingData);
		}

		// Batch Insert 또는 Batch Update 수행
		try {
			rankingRepository.saveAll(rankingDataList);
		} catch (DataAccessException e) {
			// 사용자 예외처리: DB 저장 문제 (예: INTERNAL_SERVER_ERROR)
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		}
	}


	// Redis에 있는 총점수를 db에 반영 24시간 단위
	@Override
	@Transactional
	public void updateDatabaseRankingPoints() {
		Set<ZSetOperations.TypedTuple<String>> allTeachers = zSet.reverseRangeWithScores(SCORE_KEY, 0, -1);

		List<RankingData> rankingDataList = new ArrayList<>();

		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (ZSetOperations.TypedTuple<String> teacher : allTeachers) {
				String teacherId = teacher.getValue();
				double totalPoints = (teacher.getScore() != null) ? teacher.getScore() : 0;

				RankingData teacherScore;
				try{
					teacherScore = rankingRepository.findById(Long.parseLong(teacherId))
						.orElseGet(() -> {
							RankingData newTeacher = new RankingData();
							newTeacher.setTeacherId(Long.parseLong(teacherId));
							newTeacher.setTotalScore(0L);
							return newTeacher;
						});
				} catch (NumberFormatException e) {
					throw new BusinessException(GlobalErrorCodes.NOT_FOUND_URL);
				} catch (DataAccessException e) {
					throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
				}


				long previousScore = (teacherScore.getTotalScore() != null) ? teacherScore.getTotalScore() : 0L;
				long newTotalScore = Math.round(totalPoints - (previousScore * 0.5));

				teacherScore.setTotalScore(newTotalScore);
				rankingDataList.add(teacherScore);

				// Redis 초기화
				connection.zRem(SCORE_KEY.getBytes(), teacherId.getBytes());
			}

			return null;
		});

		// Batch Insert 또는 Batch Update
		try {
			rankingRepository.saveAll(rankingDataList);
		} catch (DataAccessException e) {
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		}
	}

	// DB에 있는 총점수를 redis에 반영
	@Override
	@Transactional
	public void syncRedisRankingPoints() {
		List<RankingData> allTeachers = rankingRepository.findAll();

		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (RankingData teacher : allTeachers) {
				try {
					String teacherId = String.valueOf(teacher.getTeacherId());
					double totalPoints = teacher.getTotalScore() != null ? teacher.getTotalScore().doubleValue() : 0;
					connection.zAdd(SCORE_KEY.getBytes(), totalPoints, teacherId.getBytes());
				} catch (RedisCommandTimeoutException e) {
					throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
				} catch (RedisSystemException e) {
					throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
				}
			}
			return null;
		});
	}

	// 선생님 랭크 조회
	@Override
	public List<ZSetOperations.TypedTuple<String>> getTeachersRank() {
		try {
			Set<ZSetOperations.TypedTuple<String>> result = zSet.reverseRangeWithScores(SCORE_KEY, 0, -1);
			return (result != null) ? new ArrayList<>(result) : Collections.emptyList();
		} catch (RedisConnectionFailureException e) {
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new BusinessException(GlobalErrorCodes.INTERNAL_SERVER_ERROR);
		}
	}

}