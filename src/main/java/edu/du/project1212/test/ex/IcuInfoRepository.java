package edu.du.project1212.test.ex;

import edu.du.project1212.test.BedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IcuInfoRepository extends JpaRepository<IcuInfo, Long> {
    List<IcuInfo> findByDepartment(String department); // 과목 필터링
}
