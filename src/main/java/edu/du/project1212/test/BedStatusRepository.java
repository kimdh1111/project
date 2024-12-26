package edu.du.project1212.test;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedStatusRepository extends JpaRepository<BedStatus, Long> {
    List<BedStatus> findByStatus(String status); // 병상 상태로 필터링
}
