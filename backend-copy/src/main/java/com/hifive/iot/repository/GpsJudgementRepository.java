package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.GpsJudgement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GpsJudgementRepository extends JpaRepository<GpsJudgement, Long> {
	Optional<GpsJudgement> findByEventId(String eventId);
	List<GpsJudgement> findTop50ByOrderByJudgedAtDesc();
}
