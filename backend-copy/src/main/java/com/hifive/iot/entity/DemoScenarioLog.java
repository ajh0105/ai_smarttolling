package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "demo_scenario_log")
public class DemoScenarioLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "scenario_id")
	private Long scenarioId;

	@Column(name = "scenario_type", nullable = false, length = 50)
	private String scenarioType;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "executed_by", length = 100)
	private String executedBy;

	@Column(name = "executed_at", nullable = false)
	private LocalDateTime executedAt;
}
