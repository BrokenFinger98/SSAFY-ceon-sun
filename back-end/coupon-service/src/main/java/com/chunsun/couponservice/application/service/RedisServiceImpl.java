package com.chunsun.couponservice.application.service;

import static com.chunsun.couponservice.application.convert.DtoConverter.*;
import static com.chunsun.couponservice.application.dto.ServiceDto.*;
import static com.chunsun.couponservice.common.error.CouponErrorCodes.*;
import static com.chunsun.couponservice.presentation.dto.ControllerDto.*;
import static java.util.concurrent.TimeUnit.DAYS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import com.chunsun.couponservice.common.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void saveCouponInfo(final CouponCreateRedis couponCreateRedis) {
		final String couponInfoKey = "coupon:info:" + couponCreateRedis.couponId();
		final String couponRemainKey = "coupon:remain:" + couponCreateRedis.couponId();
		final String issuedUsersKey = "coupon:issued:" + couponCreateRedis.couponId();
		final Integer totalQuantity = couponCreateRedis.totalQuantity();
		final CouponInfoValue couponInfoValue = toCouponInfoValue(couponCreateRedis);

		final List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(final RedisOperations operations) throws DataAccessException {
				operations.multi();
				operations.opsForValue().set(couponRemainKey, totalQuantity, couponCreateRedis.validDays(), DAYS);
				operations.expire(issuedUsersKey, couponCreateRedis.validDays(), DAYS);
				operations.opsForValue().set(couponInfoKey, couponInfoValue, couponCreateRedis.validDays(), DAYS);
				return operations.exec();
			}
		});

		validateSaveTransaction(couponRemainKey, issuedUsersKey, couponInfoKey, txResults);
	}

	private void validateSaveTransaction(
		final String couponRemainKey,
		final String issuedUsersKey,
		final String couponInfoKey,
		final List<Object> txResults) {

		if (txResults == null || txResults.isEmpty() || txResults.size() < 3) {
			rollbackSaveTransaction(couponRemainKey, issuedUsersKey, couponInfoKey);
			throw new BusinessException(REDIS_SAVE_FAILED);
		}
	}

	private void rollbackSaveTransaction(
		final String couponRemainKey,
		final String issuedUsersKey,
		final String couponInfoKey) {

		try {
			redisTemplate.delete(couponRemainKey);
			redisTemplate.delete(issuedUsersKey);
			redisTemplate.delete(couponInfoKey);
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void issueCoupon(final CouponIssueRedis couponIssueRedis) {
		final String couponRemainKey = "coupon:remain:" + couponIssueRedis.couponId();
		final String issuedUsersKey = "coupon:issued:" + couponIssueRedis.couponId();
		final String memberId = couponIssueRedis.memberId().toString();

		final Boolean alreadyIssued = redisTemplate.opsForSet().isMember(issuedUsersKey, memberId);
		if (alreadyIssued != null && alreadyIssued) {
			log.info("쿠폰 중복 발급 - memberId: {}, issuedUsersKey: {}", memberId, issuedUsersKey);
			throw new BusinessException(COUPON_ALREADY_ISSUED);
		}

		final Object stockObj = redisTemplate.opsForValue().get(couponRemainKey);
		if(stockObj == null) {
			log.info("재고 없음");
		}
		int stock = stockObj != null ? Integer.parseInt(stockObj.toString()) : 0;
		if (stock <= 0) {
			log.info("쿠폰 재고 부족 - memberId: {}, issuedUsersKey: {}, 남은 수량: {}", memberId, issuedUsersKey, stock);
			throw new BusinessException(COUPON_OUT_OF_STOCK);
		}

		final List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(final RedisOperations operations) throws DataAccessException {
				operations.multi();
				operations.opsForValue().decrement(couponRemainKey);
				operations.opsForSet().add(issuedUsersKey, memberId);
				return operations.exec();
			}
		});

		validateIssueTransaction(txResults, couponRemainKey, issuedUsersKey, memberId);
	}

	private void validateIssueTransaction(final List<Object> txResults, final String remainingKey,
		final String issuedUsersKey, final String memberId) {
		if (txResults == null || txResults.isEmpty() || txResults.size() < 2) {
			log.error("쿠폰 발급 트랜잭션 실패 - memberId: {}, issuedUsersKey: {}, 결과: {}", memberId, issuedUsersKey, txResults);
			rollbackIssueTransaction(remainingKey, issuedUsersKey, memberId);
			throw new BusinessException(REDIS_ISSUE_FAILED);
		}

		final Long remainingQuantity;
		final Long addResult;
		try {
			remainingQuantity = txResults.get(0) != null ? Long.valueOf(txResults.get(0).toString()) : 0L;
			addResult = txResults.get(1) != null ? Long.valueOf(txResults.get(1).toString()) : -1L;
		} catch (final NumberFormatException e) {
			log.error("Redis 트랜잭션 결과 변환 실패 - memberId: {}, issuedUsersKey: {}. 예외: {}", memberId, issuedUsersKey, e.getMessage(), e);
			rollbackIssueTransaction(remainingKey, issuedUsersKey, memberId);
			throw new BusinessException(REDIS_ISSUE_FAILED);
		}

		if (remainingQuantity < 0) {
			log.warn("쿠폰 재고 부족 - memberId: {}, issuedUsersKey: {}, 남은 수량: {}", memberId, issuedUsersKey, remainingQuantity);
			rollbackIssueTransaction(remainingKey, issuedUsersKey, memberId);
			throw new BusinessException(COUPON_OUT_OF_STOCK);
		}

		if (addResult == 0) {
			log.warn("쿠폰 중복 발급 - memberId: {}, issuedUsersKey: {}", memberId, issuedUsersKey);
			rollbackIssueTransaction(remainingKey, issuedUsersKey, memberId);
			throw new BusinessException(COUPON_ALREADY_ISSUED);
		}
	}

	private void rollbackIssueTransaction(final String remainingKey, final String issuedUsersKey, final String memberId) {
		try {
			redisTemplate.opsForValue().increment(remainingKey);
			redisTemplate.opsForSet().remove(issuedUsersKey, memberId);
		} catch (final Exception e) {
			log.error("쿠폰 발급 롤백 중 오류 발생 - memberId: {}, issuedUsersKey: {}. 예외: {}", memberId, issuedUsersKey, e.getMessage(), e);
		}
	}

	@Override
	public SearchCouponsControllerResponse searchCoupons() {
		final Set<String> keys = redisTemplate.keys("coupon:info:*");
		final List<SearchCouponControllerResponse> couponList = new ArrayList<>();

		if (keys == null || keys.isEmpty()) {
			log.info("조회 가능한 쿠폰 정보가 없습니다.");
			return new SearchCouponsControllerResponse(couponList);
		}

		for (final String infoKey : keys) {
			try {
				final Object valueObj = redisTemplate.opsForValue().get(infoKey);
				final CouponInfoValue info = (CouponInfoValue) valueObj;

				final String remainKey = "coupon:remain:" + info.couponId();
				final Object remainObj = redisTemplate.opsForValue().get(remainKey);
				final int remainingQuantity = remainObj != null ? Integer.parseInt(remainObj.toString()) : 0;

				final SearchCouponControllerResponse couponResponse = toSearchCouponControllerResponse(info, remainingQuantity);
				couponList.add(couponResponse);
			} catch (final Exception e) {
				log.error("쿠폰 정보 키 파싱 실패: {}. 예외: {}", infoKey, e.getMessage(), e);
			}
		}
		return new SearchCouponsControllerResponse(couponList);
	}
}