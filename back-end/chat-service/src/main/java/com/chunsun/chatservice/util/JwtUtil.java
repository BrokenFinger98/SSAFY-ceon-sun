package com.chunsun.chatservice.util;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chunsun.chatservice.apiPayload.error.ChatErrorCode;
import com.chunsun.chatservice.apiPayload.exception.BusinessException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
	@Value("${token.secret}")
	private String secretKey;

	@PostConstruct
	public void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String getId(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		} catch (JwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			throw new BusinessException(ChatErrorCode.INVALID_TOKEN);
		}
	}

	public String getRole(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
			return claims.get("role", String.class);
		} catch (Exception e) {
			throw new BusinessException(ChatErrorCode.INVALID_TOKEN);
		}
	}
}

