package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.DemoScenarioLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoScenarioLogRepository extends JpaRepository<DemoScenarioLog, Long> {

	List<DemoScenarioLog> findTop20ByOrderByExecutedAtDesc();
}
