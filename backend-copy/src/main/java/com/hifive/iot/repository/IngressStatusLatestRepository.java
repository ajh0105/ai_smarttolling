package com.hifive.iot.repository;

import java.util.Optional;

import com.hifive.iot.entity.IngressStatusLatest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngressStatusLatestRepository extends JpaRepository<IngressStatusLatest, Long> {
	Optional<IngressStatusLatest> findByIngressId(String ingressId);
}
