package edu.du.project1212.test;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedInfoRepository extends JpaRepository<BedInfo, Long> {
    List<BedInfo> findBySeverity(String severity); // 중증도 필터링
}
