package com.chunsun.gatewayservice.security.filter;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.chunsun.gatewayservice.common.error.GlobalErrorCodes;
import com.chunsun.gatewayservice.common.exception.GatewayException;
import com.chunsun.gatewayservice.security.jwt.JwtProvider;
import com.chunsun.gatewayservice.util.HeaderUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
	private static final String USER_ROLE_PREFIX = "ROLE_";
	private static final String USER_ID_HEADER = "X-User-ID";
	private final JwtProvider jwtProvider;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String token = HeaderUtil.getToken(exchange);
		if (token == null || token.isBlank()) {
			return chain.filter(exchange);
		}

		Claims claims = jwtProvider.getClaims(token);
		String subject = claims.getSubject();
		if (subject == null || subject.isBlank()) {
			throw new GatewayException(GlobalErrorCodes.INVALID_LOGIN_TOKEN);
		}

		Long userId;
		try {
			userId = Long.valueOf(subject);
		} catch (NumberFormatException e) {
			throw new GatewayException(GlobalErrorCodes.INVALID_LOGIN_TOKEN);
		}

		String role = claims.get("role", String.class);
		GrantedAuthority authority = new SimpleGrantedAuthority(USER_ROLE_PREFIX + role);

		log.info("token role : " + role);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

		return chain.filter(
			exchange.mutate()
				.request(builder -> builder
					.header(USER_ID_HEADER, String.valueOf(userId))
				)
				.build()
		).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
	}
}
