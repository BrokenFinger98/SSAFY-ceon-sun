package com.chunsun.gatewayservice.config;

import java.util.List;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.chunsun.gatewayservice.common.error.GlobalErrorCodes;
import com.chunsun.gatewayservice.common.exception.GatewayException;
import com.chunsun.gatewayservice.security.filter.JwtAuthenticationFilter;
import com.chunsun.gatewayservice.security.jwt.JwtProvider;

import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {
	private final JwtProvider jwtProvider;

	private static final String[] PUBLIC_PATHS = {
		"/", "favicon.ico", "/actuator/**", "/auth-service/**",
		"/config-service/**", "/discovery-service/**"
	};

	/*
	 * 인증이 필요없는 요청 필터
	 */
	@Bean
	@Order(1)
	public SecurityWebFilterChain publicSecurityWebFilterChain(ServerHttpSecurity http) {
		http
			.securityMatcher(new OrServerWebExchangeMatcher(
				ServerWebExchangeMatchers.pathMatchers(PUBLIC_PATHS)
			))
			.csrf(csrf -> csrf.disable())
			.authorizeExchange(exchange -> exchange.anyExchange().permitAll());
		return http.build();
	}

	/*
	 * 인가가 필요한 요청 필터
	 */
	@Bean
	@Order(2)
	public SecurityWebFilterChain securedSecurityWebFilterChain(ServerHttpSecurity http) {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeExchange(exchange -> exchange
				.pathMatchers("/admin/**")
				.hasRole(Role.ADMIN)

				.pathMatchers(HttpMethod.POST, "/member-service/students",
					"/member-service/teachers")
				.hasRole(Role.GUEST)

				.pathMatchers(HttpMethod.GET,
					"/member-service/teachers/details/*")
				.hasAnyRole(Role.STUDENT, Role.ADMIN)

				.pathMatchers("/coupon-service/coupons/admin")
				.hasAnyRole(Role.ADMIN)

				.pathMatchers(HttpMethod.GET, "/member-service/students/details/*")
				.hasAnyRole(Role.TEACHER, Role.ADMIN)

				.pathMatchers("/member-service/members/ranking", "/member-service/members/*",
					"/member-service/category/*", "/member-service/category", "/member-service/categories")
				.hasAnyRole(Role.ALL)

				.pathMatchers(HttpMethod.GET, "/member-service/teachers/class/*")
				.hasAnyRole(Role.ALL)

				.pathMatchers(HttpMethod.GET, "/member-service/students",
					"/member-service/members/delete/*",
					"/member-service/members/exist/*")
				.hasRole(Role.ADMIN)

				.pathMatchers(HttpMethod.GET, "/member-service/members/*/search")
				.hasAnyRole(Role.USER)

				.pathMatchers("/member-service/s3/*")
				.hasRole(Role.ADMIN)

				.pathMatchers("/member-service/students/**", "/member-service/like")
				.hasAnyRole(Role.STUDENT, Role.ADMIN)

				.pathMatchers("/member-service/teachers/**")
				.hasAnyRole(Role.TEACHER, Role.ADMIN)

				.pathMatchers(HttpMethod.POST, "/class-service/class-requests")
				.hasAnyRole(Role.STUDENT, Role.ADMIN)

				.pathMatchers(HttpMethod.POST, "/class-service/class-requests/response")
				.hasAnyRole(Role.TEACHER, Role.ADMIN)

				.pathMatchers("/notification-service/coupons/send/all")
				.hasAnyRole(Role.ADMIN)

				.pathMatchers(HttpMethod.POST, "/coupon-service/coupons")
				.hasAnyRole(Role.STUDENT, Role.ADMIN)

				.pathMatchers("/rank-service/**")
				.hasRole(Role.ADMIN)

				.pathMatchers("/class-service/**")
				.hasAnyRole(Role.USER)

				.pathMatchers("/coupon-service/**")
				.hasAnyRole(Role.USER)

				.pathMatchers("/payment-service/**")
				.hasAnyRole(Role.STUDENT, Role.ADMIN)

				.anyExchange()
				.permitAll())
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint((exchange, ex) ->
					Mono.error(new GatewayException(GlobalErrorCodes.INVALID_LOGIN_TOKEN)))
				.accessDeniedHandler((exchange, ex) ->
					Mono.error(new GatewayException(GlobalErrorCodes.ACCESS_DENIED)))
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
				SecurityWebFiltersOrder.AUTHENTICATION);
		return http.build();
	}

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOriginPatterns(List.of("*"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setExposedHeaders(List.of("Authorization", "X-Chunsun-Authorization", "X-User-ID"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}

	@Bean
	public HttpExchangeRepository httpTraceRepository() {
		return new InMemoryHttpExchangeRepository();
	}

	private static class Role {
		static final String GUEST = "GUEST";
		static final String STUDENT = "STUDENT";
		static final String TEACHER = "TEACHER";
		static final String ADMIN = "ADMIN";

		static final String[] ALL = {GUEST, STUDENT, TEACHER, ADMIN};
		static final String[] USER = {STUDENT, TEACHER, ADMIN};
	}
}

