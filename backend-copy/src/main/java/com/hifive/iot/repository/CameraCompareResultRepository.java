package com.hifive.iot.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.entity.CameraCompareResult;
import com.hifive.iot.entity.PassageEventRecord;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CameraCompareResultRepository extends JpaRepository<CameraCompareResult, Long> {

	List<CameraCompareResult> findTop50ByOrderByComparedAtDesc();

	List<CameraCompareResult> findTop50ByLaneIdOrderByComparedAtDesc(Integer laneId);

	List<CameraCompareResult> findTop50ByIsMatchedOrderByComparedAtDesc(Boolean isMatched);

	boolean existsByFrontEvent(PassageEventRecord frontEvent);

	boolean existsByRearEvent(PassageEventRecord rearEvent);

	@Query("""
		select compareResult
		from CameraCompareResult compareResult
		where (:laneId is null or compareResult.laneId = :laneId)
		  and (:isMatched is null or compareResult.isMatched = :isMatched)
		  and (:fromTime is null or compareResult.comparedAt >= :fromTime)
		  and (:toTime is null or compareResult.comparedAt <= :toTime)
		order by compareResult.comparedAt desc
		""")
	List<CameraCompareResult> findCompareResults(
		@Param("laneId") Integer laneId,
		@Param("isMatched") Boolean isMatched,
		@Param("fromTime") LocalDateTime from,
		@Param("toTime") LocalDateTime to,
		Pageable pageable
	);
}
