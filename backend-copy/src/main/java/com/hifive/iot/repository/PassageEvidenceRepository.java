package com.hifive.iot.repository;

import java.util.Optional;

import com.hifive.iot.entity.PassageEvidence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassageEvidenceRepository extends JpaRepository<PassageEvidence, Long> {
	Optional<PassageEvidence> findByEventId(String eventId);
}
