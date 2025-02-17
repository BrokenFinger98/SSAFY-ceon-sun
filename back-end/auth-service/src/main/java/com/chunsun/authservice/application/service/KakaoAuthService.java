package com.chunsun.authservice.application.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.chunsun.authservice.application.dto.AuthDto;
import com.chunsun.authservice.common.error.AuthErrorCodes;
import com.chunsun.authservice.common.exception.BusinessException;
import com.chunsun.authservice.config.security.KakaoConfig;
import com.chunsun.authservice.util.WebClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
	private static final String TOKEN_URI = "/oauth/token";
	private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

	private final WebClientUtil webClientUtil;
	private final KakaoConfig kakaoConfig;
	private final ObjectMapper objectMapper;

	/**
	 * 카카오 인가 코드를 받아 토큰 발급 API를 호출하는 메서드
	 *
	 * @param requestDto 카카오에서 전달받은 인가 코드
	 * @return 카카오 토큰 발급 응답 (예: JSON 문자열)
	 */
	public Mono<AuthDto.KakaoTokenResponseDto> getKakaoToken(AuthDto.KakaoLoginRequestDto requestDto) {
		Map<String, String> formData = Map.of(
			"grant_type", "authorization_code",
			"client_id", kakaoConfig.getClientId(),
			"redirect_uri", kakaoConfig.getRedirectUri(),
			"code", requestDto.getAuthCode()
		);
		return webClientUtil.postFormData(TOKEN_URI, formData, String.class)
			.flatMap(response -> {
				try {
					return Mono.just(objectMapper.readValue(response, AuthDto.KakaoTokenResponseDto.class));
				} catch (Exception e) {
					log.error("kakaoLogin Error Code : {} ", e);
					return Mono.error(new BusinessException(AuthErrorCodes.INVALID_KAKAO_AUTHORIZATION_CODE));
				}
			});
	}

	/**
	 * 카카오 엑세스 코드를 받아 토큰 발급 API를 호출하는 메서드
	 *
	 * @param accessToken 카카오에서 전달받은 엑세스 코드
	 * @return 카카오 유저 정보 JSON 문자열
	 */
	public Mono<AuthDto.KakaoUserInfoDto> getKakaoUserInfo(String accessToken) {
		return webClientUtil.getWithAuth(USER_INFO_URI, accessToken, String.class)
			.flatMap(response -> {
				try {
					return Mono.just(objectMapper.readValue(response, AuthDto.KakaoUserInfoDto.class));
				} catch (Exception e) {
					log.error("카카오 정보 요청 실패 : ", e);
					return Mono.error(new BusinessException(AuthErrorCodes.KAKAO_INFO_DATA_SEARCH_FAIL));
				}
			});
	}
}
