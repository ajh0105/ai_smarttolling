package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.EdgeStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeStatusHistoryRepository extends JpaRepository<EdgeStatusHistory, Long> {
	List<EdgeStatusHistory> findTop100ByDeviceIdOrderByReceivedAtDesc(String deviceId);
}
