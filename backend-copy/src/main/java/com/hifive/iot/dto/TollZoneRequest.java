package com.hifive.iot.dto;

public record TollZoneRequest(
	String zoneName,
	Double minLatitude,
	Double maxLatitude,
	Double minLongitude,
	Double maxLongitude,
	Double northWestLatitude,
	Double northWestLongitude,
	Double northEastLatitude,
	Double northEastLongitude,
	Double southEastLatitude,
	Double southEastLongitude,
	Double southWestLatitude,
	Double southWestLongitude,
	Integer baseFare
) {
}
