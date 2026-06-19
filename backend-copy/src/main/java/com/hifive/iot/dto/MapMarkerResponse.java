package com.hifive.iot.dto;

import com.hifive.iot.entity.MapMarkerRecord;

public record MapMarkerResponse(
	String name,
	Double x,
	Double y,
	Double labelX,
	Double labelY
) {
	public static MapMarkerResponse from(MapMarkerRecord record) {
		return new MapMarkerResponse(
			record.getMarkerName(),
			record.getX(),
			record.getY(),
			record.getLabelX(),
			record.getLabelY()
		);
	}
}
