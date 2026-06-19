package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.PlateRecognitionRequest;
import com.hifive.iot.dto.TollChargeResponse;
import com.hifive.iot.dto.TollDecisionResponse;
import com.hifive.iot.dto.TollZoneRequest;
import com.hifive.iot.dto.TollZoneResponse;
import com.hifive.iot.service.TollChargeService;
import com.hifive.iot.service.TollZoneService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/toll")
public class TollController {

	private final TollZoneService tollZoneService;
	private final TollChargeService tollChargeService;

	public TollController(TollZoneService tollZoneService, TollChargeService tollChargeService) {
		this.tollZoneService = tollZoneService;
		this.tollChargeService = tollChargeService;
	}

	@GetMapping("/zones")
	public List<TollZoneResponse> zones() {
		return tollZoneService.activeZones();
	}

	@PostMapping("/zones")
	@ResponseStatus(HttpStatus.CREATED)
	public TollZoneResponse createZone(@RequestBody TollZoneRequest request) {
		return tollZoneService.create(request);
	}

	@PostMapping("/plate-recognitions")
	public TollDecisionResponse recognizePlate(@RequestBody PlateRecognitionRequest request) {
		return tollChargeService.recognizePlate(request);
	}

	@GetMapping("/history/latest")
	public List<TollChargeResponse> latestCharges() {
		return tollChargeService.latestCharges();
	}
}
