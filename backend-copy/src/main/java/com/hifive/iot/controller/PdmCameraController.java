package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.PdmCameraDetailResponse;
import com.hifive.iot.dto.PdmCameraResponse;
import com.hifive.iot.service.PdmCameraService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/cameras")
public class PdmCameraController {

	private final PdmCameraService pdmCameraService;

	@GetMapping
	public ResponseEntity<List<PdmCameraResponse>> getCameras() {
		return ResponseEntity.ok(pdmCameraService.getCameras());
	}

	@GetMapping("/{cameraId}")
	public ResponseEntity<PdmCameraDetailResponse> getCameraDetail(@PathVariable Long cameraId) {
		return ResponseEntity.ok(pdmCameraService.getCameraDetail(cameraId));
	}
}
