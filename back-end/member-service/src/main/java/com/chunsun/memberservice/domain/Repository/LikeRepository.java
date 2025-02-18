package com.chunsun.memberservice.domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunsun.memberservice.domain.Entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

	@Modifying
	@Query("DELETE FROM Like l WHERE l.likerId = :likerId AND l.likeeId = :likeeId")
	int deleteByLikerIdAndLikeeId(@Param("likerId") Long likerId, @Param("likeeId") Long likeeId);

}
