package com.hifive.iot.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "camera_quality_metric")
public class CameraQualityMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metric_id")
	private Long metricId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "camera_id", nullable = false)
	private CameraDevice camera;

	@Column(name = "lane_id")
	private Integer laneId;

	@Column(name = "bucket_start", nullable = false)
	private LocalDateTime bucketStart;

	@Column(name = "bucket_end", nullable = false)
	private LocalDateTime bucketEnd;

	@Column(name = "avg_ocr_confidence", nullable = false)
	private Double avgOcrConfidence;

	@Column(name = "success_rate", nullable = false)
	private Double successRate;

	@Column(name = "missing_rate", nullable = false)
	private Double missingRate;

	@Column(name = "match_rate", nullable = false)
	private Double matchRate;

	@Column(name = "mismatch_rate", nullable = false)
	private Double mismatchRate;

	@Column(name = "event_count", nullable = false)
	private Integer eventCount;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
