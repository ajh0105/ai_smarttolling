package com.hifive.iot.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "maintenance_alert")
public class MaintenanceAlert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alert_id")
	private Long alertId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "analysis_id", nullable = false)
	private PdmAnalysisResult analysis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "camera_id", nullable = false)
	private CameraDevice camera;

	@Column(name = "lane_id")
	private Integer laneId;

	@Column(name = "risk_level", nullable = false, length = 20)
	private String riskLevel;

	@Column(name = "alert_title", nullable = false, length = 200)
	private String alertTitle;

	@Column(name = "alert_message", nullable = false, columnDefinition = "TEXT")
	private String alertMessage;

	@Column(name = "reason_text", columnDefinition = "TEXT")
	private String reasonText;

	@Column(name = "recommended_action", columnDefinition = "TEXT")
	private String recommendedAction;

	@Column(nullable = false, length = 20)
	private String status;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public void changeStatus(String status) {
		this.status = status;
	}
}
