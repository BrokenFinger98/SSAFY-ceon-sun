package com.chunsun.gatewayservice.security.jwt;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chunsun.gatewayservice.common.error.GlobalErrorCodes;
import com.chunsun.gatewayservice.common.exception.GatewayException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {
	@Value("${token.secret}")
	private String secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public Claims getClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(this.secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			log.error("Failed to parse JWT claims: {}", e.getMessage());
			throw new GatewayException(GlobalErrorCodes.INVALID_LOGIN_TOKEN);
		}
	}
}
