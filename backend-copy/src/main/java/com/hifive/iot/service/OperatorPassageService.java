package com.hifive.iot.service;

import java.util.List;

import com.hifive.iot.dto.PassageEventResponse;
import com.hifive.iot.entity.PassageEvidence;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.PassageEvidenceRepository;

import org.springframework.stereotype.Service;

@Service
public class OperatorPassageService {
	private final PassageEventRepository passageEventRepository;
	private final PassageEvidenceRepository evidenceRepository;

	public OperatorPassageService(PassageEventRepository passageEventRepository, PassageEvidenceRepository evidenceRepository) {
		this.passageEventRepository = passageEventRepository;
		this.evidenceRepository = evidenceRepository;
	}

	public List<PassageEventResponse> list() {
		return passageEventRepository.findTop50ByEventTimeIsNotNullAndPlateTextIsNotNullAndLaneNoIsNotNullOrderByEventTimeDesc().stream()
			.map(event -> PassageEventResponse.from(event, evidenceRepository.findByEventId(event.getEventId()).orElse(null)))
			.toList();
	}

	public PassageEventResponse find(String eventId) {
		var event = passageEventRepository.findByEventId(eventId)
			.orElseThrow(() -> new IllegalArgumentException("passage event not found"));
		PassageEvidence evidence = evidenceRepository.findByEventId(eventId).orElse(null);
		return PassageEventResponse.from(event, evidence);
	}
}
