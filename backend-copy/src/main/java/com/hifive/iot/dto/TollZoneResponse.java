package com.hifive.iot.dto;

public record TollZoneResponse(
	Long id,
	String zoneName,
	Double minLatitude,
	Double maxLatitude,
	Double minLongitude,
	Double maxLongitude,
	Integer baseFare,
	Boolean active
) {
}
