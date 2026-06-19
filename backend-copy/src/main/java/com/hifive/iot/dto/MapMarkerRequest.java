package com.hifive.iot.dto;

public record MapMarkerRequest(
	String name,
	Double x,
	Double y,
	Double labelX,
	Double labelY
) {
}
