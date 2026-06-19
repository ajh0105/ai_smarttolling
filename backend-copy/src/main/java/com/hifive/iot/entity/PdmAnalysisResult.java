package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "pdm_analysis_result")
public class PdmAnalysisResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "analysis_id")
	private Long analysisId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "camera_id", nullable = false)
	private CameraDevice camera;

	@Column(name = "lane_id")
	private Integer laneId;

	@Column(name = "analysis_start", nullable = false)
	private LocalDateTime analysisStart;

	@Column(name = "analysis_end", nullable = false)
	private LocalDateTime analysisEnd;

	@Column(name = "health_score", nullable = false)
	private Double healthScore;

	@Column(name = "risk_level", nullable = false, length = 20)
	private String riskLevel;

	@Column(name = "model_type", nullable = false, length = 30)
	private String modelType;

	@Column(name = "model_version", length = 50)
	private String modelVersion;

	@Column(name = "reason_code", length = 50)
	private String reasonCode;

	@Column(name = "reason_text", columnDefinition = "TEXT")
	private String reasonText;

	@Column(name = "recommended_action", columnDefinition = "TEXT")
	private String recommendedAction;

	@Column(name = "trend_summary", columnDefinition = "TEXT")
	private String trendSummary;

	@Column(name = "analyzed_at", nullable = false)
	private LocalDateTime analyzedAt;
}
