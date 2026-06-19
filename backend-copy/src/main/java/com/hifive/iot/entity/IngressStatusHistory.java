package com.hifive.iot.entity;

import jakarta.persistence.Entity;

@Entity
public class IngressStatusHistory extends IngressStatusLatest {
	protected IngressStatusHistory() {
	}

	public IngressStatusHistory(String ingressId) {
		super(ingressId);
	}
}
