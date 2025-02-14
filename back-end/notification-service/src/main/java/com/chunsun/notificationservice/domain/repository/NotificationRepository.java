package com.chunsun.notificationservice.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.chunsun.notificationservice.domain.entity.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
	List<Notification> findByTargetUserId(String userId);

	@Query(sort = "{ 'createdAt': -1 }")
	List<Notification> findByTargetUserIdAndIsReadFalse(String userId);

	@Query(sort = "{ 'createdAt': -1 }")
	List<Notification> findByTargetUserIdAndIsReadTrue(String userId);

	boolean existsByTargetUserIdAndIsReadFalse(String userId);
}
