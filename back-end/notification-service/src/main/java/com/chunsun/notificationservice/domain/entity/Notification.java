package com.chunsun.notificationservice.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.chunsun.notificationservice.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "notifications")
@CompoundIndex(name = "target_read_createdAt_idx", def = "{'targetUserId': 1, 'createdAt': -1}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {
	@Id
	private String id;        // 자동 상승

	private String sendUserId;    // 선택

	private String targetUserId;

	private String type;

	private String message;

	private boolean isRead;
}
