package com.hifive.iot.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.SettlementCandidate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementCandidateRepository extends JpaRepository<SettlementCandidate, Long> {
	Optional<SettlementCandidate> findByEventId(String eventId);
	List<SettlementCandidate> findTop50ByOrderByCreatedAtDesc();
	List<SettlementCandidate> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
	long countByStatus(String status);
}
