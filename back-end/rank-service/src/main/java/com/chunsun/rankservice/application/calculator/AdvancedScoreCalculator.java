package com.chunsun.rankservice.application.calculator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Qualifier("advancedCalculator")
@RequiredArgsConstructor
public class AdvancedScoreCalculator implements ScoreCalculator {

	private final RankingWeightProperties rankingWeightProperties;

	// 점수 산출 요소가 추가될 경우 여기에 업데이트
	@Override
	public double calculateScore(ScoreFactors factors) {

		double result =  factors.getViewScore() * rankingWeightProperties.getViewScore() +
						factors.getClassScore() * rankingWeightProperties.getClassScore();

		return result;
	}
}
