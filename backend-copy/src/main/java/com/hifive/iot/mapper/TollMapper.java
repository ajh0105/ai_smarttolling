package com.hifive.iot.mapper;

import com.hifive.iot.dto.TollChargeResponse;
import com.hifive.iot.dto.TollZoneResponse;
import com.hifive.iot.entity.TollHistoryRecord;
import com.hifive.iot.entity.TollZoneRecord;

public final class TollMapper {

	private TollMapper() {
	}

	public static TollZoneResponse toZoneResponse(TollZoneRecord zone) {
		return new TollZoneResponse(
			zone.getTollZoneId(),
			zone.getZoneName(),
			zone.getMinLatitude(),
			zone.getMaxLatitude(),
			zone.getMinLongitude(),
			zone.getMaxLongitude(),
			zone.getBaseFare(),
			zone.getActive()
		);
	}

	public static TollChargeResponse toChargeResponse(TollHistoryRecord charge) {
		return new TollChargeResponse(
			charge.getTollHistoryId(),
			charge.getPlateNumber(),
			charge.getGpsDeviceId(),
			charge.getLaneId(),
			charge.getTollZoneId(),
			charge.getGpsTelemetryId(),
			charge.getAmount(),
			charge.getPaymentStatus(),
			charge.getSourceType(),
			charge.getPlateConfidence(),
			charge.getChargedAt()
		);
	}
}
