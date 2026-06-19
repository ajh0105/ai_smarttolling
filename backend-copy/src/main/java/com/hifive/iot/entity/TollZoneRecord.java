package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "toll_zone")
public class TollZoneRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "toll_zone_id")
	private Long tollZoneId;

	@Column(name = "zone_name", nullable = false, length = 80)
	private String zoneName;

	@Column(name = "min_latitude", nullable = false)
	private Double minLatitude;

	@Column(name = "max_latitude", nullable = false)
	private Double maxLatitude;

	@Column(name = "min_longitude", nullable = false)
	private Double minLongitude;

	@Column(name = "max_longitude", nullable = false)
	private Double maxLongitude;

	@Column(name = "base_fare", nullable = false)
	private Integer baseFare;

	@Column(nullable = false)
	private Boolean active;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected TollZoneRecord() {
	}

	public TollZoneRecord(
		String zoneName,
		Double minLatitude,
		Double maxLatitude,
		Double minLongitude,
		Double maxLongitude,
		Integer baseFare,
		Boolean active,
		LocalDateTime createdAt
	) {
		this.zoneName = zoneName;
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
		this.baseFare = baseFare;
		this.active = active;
		this.createdAt = createdAt;
	}

	public boolean contains(Double latitude, Double longitude) {
		return latitude != null && longitude != null
			&& latitude >= minLatitude && latitude <= maxLatitude
			&& longitude >= minLongitude && longitude <= maxLongitude;
	}

	public Long getTollZoneId() {
		return tollZoneId;
	}

	public String getZoneName() {
		return zoneName;
	}

	public Double getMinLatitude() {
		return minLatitude;
	}

	public Double getMaxLatitude() {
		return maxLatitude;
	}

	public Double getMinLongitude() {
		return minLongitude;
	}

	public Double getMaxLongitude() {
		return maxLongitude;
	}

	public Integer getBaseFare() {
		return baseFare;
	}

	public Boolean getActive() {
		return active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
