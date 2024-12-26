package edu.du.project1212;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyHospitalRepository extends JpaRepository<EmergencyHospitalInfo, Long> {
}
