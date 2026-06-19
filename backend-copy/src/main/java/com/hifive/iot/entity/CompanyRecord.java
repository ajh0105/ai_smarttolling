package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_company")
public class CompanyRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_id")
	private Long companyId;

	@Column(name = "company_name", nullable = false, length = 120)
	private String companyName;

	@Column(name = "owner_name", nullable = false, length = 80)
	private String ownerName;

	@Column(nullable = false, length = 30)
	private String phone;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(name = "center_count", nullable = false)
	private Integer centerCount;

	@Column(nullable = false, length = 20)
	private String status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected CompanyRecord() {
	}

	public CompanyRecord(
		String companyName,
		String ownerName,
		String phone,
		String email,
		Integer centerCount,
		String status,
		LocalDateTime createdAt
	) {
		this.companyName = companyName;
		this.ownerName = ownerName;
		this.phone = phone;
		this.email = email;
		this.centerCount = centerCount;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public Integer getCenterCount() {
		return centerCount;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
