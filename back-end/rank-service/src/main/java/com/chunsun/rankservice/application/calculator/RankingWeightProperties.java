package com.chunsun.rankservice.application.calculator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "ranking.weight")
// 랭킹 산출 요소 가중치
public class RankingWeightProperties {

	private double viewScore;
	private double classScore;
}
