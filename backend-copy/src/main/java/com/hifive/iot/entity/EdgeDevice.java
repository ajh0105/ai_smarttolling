package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EdgeDevice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String deviceId;

	private String siteId;
	private Integer laneNo;
	private String cameraRole;
	private String label;
	private LocalDateTime registeredAt;
	private Boolean active;

	protected EdgeDevice() {
	}

	public EdgeDevice(String deviceId, String siteId, Integer laneNo, String cameraRole, String label) {
		this.deviceId = deviceId;
		this.siteId = siteId;
		this.laneNo = laneNo;
		this.cameraRole = cameraRole;
		this.label = label;
		this.registeredAt = LocalDateTime.now();
		this.active = true;
	}

	public Long getId() { return id; }
	public String getDeviceId() { return deviceId; }
	public String getSiteId() { return siteId; }
	public Integer getLaneNo() { return laneNo; }
	public String getCameraRole() { return cameraRole; }
	public String getLabel() { return label; }
	public LocalDateTime getRegisteredAt() { return registeredAt; }
	public Boolean getActive() { return active; }
}
