package com.hifive.iot.entity;

import jakarta.persistence.Entity;

@Entity
public class EdgeStatusHistory extends EdgeStatusLatest {
	protected EdgeStatusHistory() {
	}

	public EdgeStatusHistory(String deviceId) {
		super(deviceId);
	}
}
