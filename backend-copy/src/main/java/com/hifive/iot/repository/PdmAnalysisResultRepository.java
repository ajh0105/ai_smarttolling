package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.PdmAnalysisResult;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PdmAnalysisResultRepository extends JpaRepository<PdmAnalysisResult, Long> {

	Optional<PdmAnalysisResult> findTopByCamera_CameraIdOrderByAnalyzedAtDesc(Long cameraId);

	List<PdmAnalysisResult> findTop50ByOrderByAnalyzedAtDesc();
}
