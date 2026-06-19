package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_map_marker")
public class MapMarkerRecord {

	@Id
	@Column(name = "marker_name", length = 80)
	private String markerName;

	@Column(nullable = false)
	private Double x;

	@Column(nullable = false)
	private Double y;

	@Column(name = "label_x", nullable = false)
	private Double labelX;

	@Column(name = "label_y", nullable = false)
	private Double labelY;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected MapMarkerRecord() {
	}

	public MapMarkerRecord(String markerName, Double x, Double y, Double labelX, Double labelY, LocalDateTime updatedAt) {
		this.markerName = markerName;
		this.x = x;
		this.y = y;
		this.labelX = labelX;
		this.labelY = labelY;
		this.updatedAt = updatedAt;
	}

	public void update(Double x, Double y, Double labelX, Double labelY) {
		this.x = x;
		this.y = y;
		this.labelX = labelX;
		this.labelY = labelY;
		this.updatedAt = LocalDateTime.now();
	}

	public String getMarkerName() {
		return markerName;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Double getLabelX() {
		return labelX;
	}

	public Double getLabelY() {
		return labelY;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
