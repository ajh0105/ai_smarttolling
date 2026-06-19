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
@Table(name = "camera_compare_result")
public class CameraCompareResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compare_id")
	private Long compareId;

	@Column(name = "event_group_key", nullable = false, length = 100)
	private String eventGroupKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "front_event_id")
	private PassageEventRecord frontEvent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rear_event_id")
	private PassageEventRecord rearEvent;

	@Column(name = "lane_id", nullable = false)
	private Integer laneId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "front_camera_id", nullable = false)
	private CameraDevice frontCamera;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rear_camera_id", nullable = false)
	private CameraDevice rearCamera;

	@Column(name = "front_plate_text", length = 30)
	private String frontPlateText;

	@Column(name = "rear_plate_text", length = 30)
	private String rearPlateText;

	@Column(name = "is_matched", nullable = false)
	private Boolean isMatched;

	@Column(name = "mismatch_type", length = 30)
	private String mismatchType;

	@Column(name = "confidence_gap")
	private Double confidenceGap;

	@Column(name = "compared_at", nullable = false)
	private LocalDateTime comparedAt;
}
