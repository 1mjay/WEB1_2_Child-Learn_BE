package com.prgrms.ijuju.domain.point.repository;

import com.prgrms.ijuju.domain.point.entity.PointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.ijuju.domain.point.entity.PointDetails;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointDetailsRepository extends JpaRepository<PointDetails, Long> {
    List<PointDetails> findByMemberIdAndCreatedAtBetweenAndPointType(Long memberId, LocalDateTime startDate, LocalDateTime endDate, PointType type);
}
