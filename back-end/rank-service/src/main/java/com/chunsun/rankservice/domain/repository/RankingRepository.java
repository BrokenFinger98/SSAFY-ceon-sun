package com.chunsun.rankservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chunsun.rankservice.domain.entity.RankingData;

public interface RankingRepository extends JpaRepository<RankingData, Long> {

}
