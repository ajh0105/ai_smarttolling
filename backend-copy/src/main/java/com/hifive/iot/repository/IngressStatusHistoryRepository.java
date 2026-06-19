package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.IngressStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngressStatusHistoryRepository extends JpaRepository<IngressStatusHistory, Long> {
	List<IngressStatusHistory> findTop100ByOrderByReceivedAtDesc();
}
