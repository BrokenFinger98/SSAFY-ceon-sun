package com.chunsun.notificationservice.config.database;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import com.chunsun.notificationservice.domain.entity.Notification;

@Configuration
@EnableMongoAuditing
public class MongoDBConfig {

	@Value("${mongodb.notification.expire}")
	private long expire;

	@Bean
	public boolean createTTLIndex(MongoTemplate mongoTemplate) {
		mongoTemplate.indexOps(Notification.class)
			.ensureIndex(new Index().on("createdAt", Sort.Direction.DESC)
				.expire(expire, TimeUnit.SECONDS));

		return true;
	}
}
