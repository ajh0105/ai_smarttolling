package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PassageEvidence {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String eventId;
	private String eventImageUrl;
	private String cropImageUrl;
	private String storagePath;
	private LocalDateTime createdAt;

	protected PassageEvidence() {
	}

	public PassageEvidence(String eventId, String eventImageUrl, String cropImageUrl, String storagePath) {
		this.eventId = eventId;
		this.eventImageUrl = eventImageUrl;
		this.cropImageUrl = cropImageUrl;
		this.storagePath = storagePath;
		this.createdAt = LocalDateTime.now();
	}

	public void updateEventImageUrl(String eventImageUrl, String storagePath) {
		this.eventImageUrl = eventImageUrl;
		this.storagePath = storagePath;
	}

	public void updateCropImageUrl(String cropImageUrl, String storagePath) {
		this.cropImageUrl = cropImageUrl;
		this.storagePath = storagePath;
	}

	public String getEventId() { return eventId; }
	public String getEventImageUrl() { return eventImageUrl; }
	public String getCropImageUrl() { return cropImageUrl; }
	public String getStoragePath() { return storagePath; }
	public LocalDateTime getCreatedAt() { return createdAt; }
}
