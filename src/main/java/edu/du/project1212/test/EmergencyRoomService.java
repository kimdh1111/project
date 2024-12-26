package edu.du.project1212.test;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyRoomService {
    private final BedInfoRepository bedInfoRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final BedStatusRepository bedStatusRepository;

    public EmergencyRoomService(BedInfoRepository bedInfoRepository,
                                EmergencyContactRepository emergencyContactRepository,
                                BedStatusRepository bedStatusRepository) {
        this.bedInfoRepository = bedInfoRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.bedStatusRepository = bedStatusRepository;
    }

    public List<BedInfo> getBedInfo(String severity) {
        if (severity != null) {
            return bedInfoRepository.findBySeverity(severity);
        }
        return bedInfoRepository.findAll();  // severity 필터링 없이 전체 반환
    }

    public List<EmergencyContact> getEmergencyContacts(String bedType) {
        if (bedType != null) {
            return emergencyContactRepository.findByBedType(bedType);
        }
        return emergencyContactRepository.findAll();  // bedType 필터링 없이 전체 반환
    }

    public List<BedStatus> getBedStatus(String status) {
        if (status != null) {
            return bedStatusRepository.findByStatus(status);
        }
        return bedStatusRepository.findAll();  // status 필터링 없이 전체 반환
    }
}
