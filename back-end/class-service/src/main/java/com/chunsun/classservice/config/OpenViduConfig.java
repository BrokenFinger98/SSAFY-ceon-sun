package com.chunsun.classservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.openvidu.java.client.OpenVidu;

@Configuration
public class OpenViduConfig {

	@Value("${OPENVIDU_URL}")
	private String openViduUrl;

	@Value("${OPENVIDU_SECRET}")
	private String openViduSecret;

	@Bean
	public OpenVidu openVidu() {
		return new OpenVidu(openViduUrl, openViduSecret);
	}
}
