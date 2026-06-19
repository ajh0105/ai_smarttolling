package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.BoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {
	List<BoardPost> findAllByOrderByPostIdDesc();
}
