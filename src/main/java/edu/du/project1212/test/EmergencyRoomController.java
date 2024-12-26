package edu.du.project1212.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmergencyRoomController {
    private final EmergencyRoomService emergencyRoomService;

    public EmergencyRoomController(EmergencyRoomService emergencyRoomService) {
        this.emergencyRoomService = emergencyRoomService;
    }

    // 병상 정보 (중증도별 필터링 가능)
    @GetMapping("/api/bed-info")
    public List<BedInfo> getBedInfo(@RequestParam(required = false) String severity) {
        return emergencyRoomService.getBedInfo(severity);
    }

    // 응급실 연락처 (병상 유형별 필터링 가능)
    @GetMapping("/api/emergency-contacts")
    public List<EmergencyContact> getEmergencyContacts(@RequestParam(required = false) String bedType) {
        return emergencyRoomService.getEmergencyContacts(bedType);
    }

    // 병상 상태 (상태별 필터링 가능)
    @GetMapping("/api/bed-status")
    public List<BedStatus> getBedStatus(@RequestParam(required = false) String status) {
        return emergencyRoomService.getBedStatus(status);
    }
}
