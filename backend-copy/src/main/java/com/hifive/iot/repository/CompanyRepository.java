package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.CompanyRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyRecord, Long> {

	boolean existsByEmail(String email);

	List<CompanyRecord> findAllByOrderByCompanyIdAsc();
}
