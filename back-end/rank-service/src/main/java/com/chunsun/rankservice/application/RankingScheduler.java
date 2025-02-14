package com.chunsun.rankservice.application;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chunsun.rankservice.application.service.RankingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RankingScheduler {

	private final RankingService rankingService;

	@Scheduled(cron = "0 0 0/3 * * *")
	public void mergeRealTimeViewsAndClass() {

		rankingService.mergeRealTimeData();
	}

	@Scheduled(cron = "0 10 3 * * *")
	public void mergeRedisAndDB() {

		rankingService.updateDatabaseRankingPoints();
		rankingService.syncRedisRankingPoints();
	}
}
