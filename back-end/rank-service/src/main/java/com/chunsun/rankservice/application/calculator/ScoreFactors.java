package com.chunsun.rankservice.application.calculator;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 랭킹 산출 요소
public class ScoreFactors {

	private double viewScore;
	private double classScore;
}
