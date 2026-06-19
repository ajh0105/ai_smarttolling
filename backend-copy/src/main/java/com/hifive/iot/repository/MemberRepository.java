package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

	List<Member> findAllByOrderByCreatedAtDesc();
}
