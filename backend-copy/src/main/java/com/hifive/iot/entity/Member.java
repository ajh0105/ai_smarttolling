package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class Member {

	@Id
	@Column(name = "email", nullable = false, length = 120)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Column(name = "member_name", nullable = false, length = 80)
	private String memberName;

	@Column(name = "plate_number", length = 20)
	private String plateNumber;

	@Column(name = "role", nullable = false, length = 30)
	private String role;

	@Column(name = "assigned_dashboard_id", length = 80)
	private String assignedDashboardId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected Member() {
	}

	public Member(
		String email,
		String passwordHash,
		String memberName,
		String plateNumber,
		String role,
		String assignedDashboardId,
		LocalDateTime createdAt
	) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.memberName = memberName;
		this.plateNumber = plateNumber;
		this.role = role;
		this.assignedDashboardId = assignedDashboardId;
		this.createdAt = createdAt;
	}

	public boolean isMasterAdmin() {
		return "MASTER_ADMIN".equals(role);
	}

	public void promoteToMasterAdmin(String passwordHash) {
		this.passwordHash = passwordHash;
		this.memberName = "admin";
		this.plateNumber = null;
		this.role = "MASTER_ADMIN";
		this.assignedDashboardId = null;
	}

	public void assignDashboard(String assignedDashboardId) {
		this.assignedDashboardId = assignedDashboardId;
	}

	public String email() {
		return email;
	}

	public String memberId() {
		return email;
	}

	public String passwordHash() {
		return passwordHash;
	}

	public String memberName() {
		return memberName;
	}

	public String plateNumber() {
		return plateNumber;
	}

	public String role() {
		return role;
	}

	public String assignedDashboardId() {
		return assignedDashboardId;
	}

	public LocalDateTime createdAt() {
		return createdAt;
	}
}
