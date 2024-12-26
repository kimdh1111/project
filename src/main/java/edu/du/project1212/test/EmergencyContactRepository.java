package edu.du.project1212.test;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByBedType(String bedType);  // 병상 유형으로 필터링

}
