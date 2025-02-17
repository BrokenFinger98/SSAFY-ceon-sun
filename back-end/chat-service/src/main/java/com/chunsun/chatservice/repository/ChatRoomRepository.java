package com.chunsun.chatservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunsun.chatservice.domain.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findByStudentIdAndTeacherId(Long studentId, Long teacherId);

	@Query("SELECT c FROM ChatRoom c WHERE c.studentId = :userId OR c.teacherId = :userId")
	List<ChatRoom> findAllByUserId(@Param("userId") Long userId);

}
