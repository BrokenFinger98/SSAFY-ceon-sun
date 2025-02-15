package com.chunsun.rankservice.application.calculator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BasicScoreCalculator implements ScoreCalculator {

	private final RankingWeightProperties rankingWeightProperties;

	// 현재 점수 산출요소
	@Override
	public double calculateScore(ScoreFactors factors) {

		double result =  factors.getViewScore() * rankingWeightProperties.getViewScore() +
						factors.getClassScore() * rankingWeightProperties.getClassScore();

		return result;
	}
}
