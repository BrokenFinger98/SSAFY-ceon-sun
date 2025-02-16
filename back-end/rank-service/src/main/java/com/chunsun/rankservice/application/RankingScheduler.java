package com.chunsun.rankservice.application;

import java.time.Instant;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunsun.rankservice.application.service.RankingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RankingScheduler {

	private final RankingService rankingService;
	private final RedisTemplate<String, String> redisTemplate;

	private final Instant serverStartTime = Instant.now();

	@Scheduled(cron = "0 0 0/3 * * *")
	public void mergeRealTimeViewsAndClass() {

		rankingService.mergeRealTimeData();
	}

	@Scheduled(cron = "0 10 3 * * *")
	@Transactional
	public void mergeRedisAndDB() {

		rankingService.updateDatabaseRankingPoints();
		rankingService.syncRedisRankingPoints();
	}

	@Scheduled(fixedRate = 60000)
	public void checkRedisRanking() {

		if(Instant.now().isBefore(serverStartTime.plusSeconds(10800))) return;

		boolean exists = redisTemplate.hasKey("teacher:score");

		if(!exists) {
			System.out.println("Redis 랭킹 데이터 유실");

			rankingService.syncRedisRankingPoints();
		}
	}
}
