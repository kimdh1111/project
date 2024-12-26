package edu.du.project1212.test;

import edu.du.project1212.EmergencyHospitalInfo;
import edu.du.project1212.EmergencyHospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BedStatusService {

    @Autowired
    private BedStatusRepository bedStatusRepository;
    @Autowired
    private EmergencyHospitalRepository emergencyHospitalRepository;



    @Transactional
    public void syncBedStatus() {
        // EmergencyHospitalInfo 테이블에서 병원 데이터를 가져옵니다.
        List<EmergencyHospitalInfo> hospitalData = emergencyHospitalRepository.findAll();
        System.out.println("Number of hospitals: " + hospitalData.size());  // 병원 데이터 수 출력

        // 병원 데이터를 BedStatus로 변환하여 저장
        List<BedStatus> bedStatusList = new ArrayList<>();
        for (EmergencyHospitalInfo hospital : hospitalData) {
            // 예시: 병상 종류별로 병상 수를 저장하고 중증도 설정
            addBedStatus(bedStatusList, "중환자실 음압격리", hospital.getHv17(), hospital.getHv17(), "Critical", hospital.getCalltime(), hospital.getDutyAddr(), hospital.getDutyName());
            addBedStatus(bedStatusList, "중환자실 일반격리", hospital.getHv18(), hospital.getHv18(), "Moderate", hospital.getCalltime(), hospital.getDutyAddr(), hospital.getDutyName());
            addBedStatus(bedStatusList, "입원실 음압격리", hospital.getHv19(), hospital.getHv19(), "Mild", hospital.getCalltime(), hospital.getDutyAddr(), hospital.getDutyName());

            // 여기에 추가적인 병상 종류를 처리하는 코드 작성
        }

        // bedStatusList가 비어 있지 않음을 확인
        System.out.println("BedStatus List size before saving: " + bedStatusList.size());  // 리스트 크기 출력

        if (!bedStatusList.isEmpty()) {
            // 데이터를 DB에 저장
            bedStatusRepository.saveAll(bedStatusList);
            bedStatusRepository.flush();  // 즉시 DB에 반영
            System.out.println("BedStatus data saved successfully.");
        } else {
            System.out.println("No BedStatus data to save.");
        }
    }

    private void addBedStatus(List<BedStatus> bedStatusList, String bedType, String availableBeds, String usedBeds, String status, String callTime, String dutyAddr, String dutyName) {
        BedStatus bedStatus = new BedStatus();
        bedStatus.setBedType(bedType);

        // 값 출력 (디버깅용)
        System.out.println("Adding BedStatus: " + bedType + ", Available: " + availableBeds + ", Used: " + usedBeds);

        // 값 변환 후 출력 (디버깅용)
        int available = parseIntOrDefault(availableBeds, 0);
        int used = parseIntOrDefault(usedBeds, 0);
        System.out.println("Converted Available: " + available + ", Converted Used: " + used);

        bedStatus.setCalltime(callTime);  // calltime을 그대로 String으로 저장
        bedStatus.setDutyAddr(dutyAddr);  // 지역 설정 시도
        bedStatus.setDutyName(dutyName);  // 병원 이름
        bedStatus.setAvailableBeds(available);  // 숫자로 변환하여 저장
        bedStatus.setUsedBeds(used);  // 숫자로 변환하여 저장
        bedStatus.setStatus(status);  // 병상 상태 설정
        // EmergencyHospitalInfo에서 가져온 calltime을 그대로 저장

        bedStatusList.add(bedStatus);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing value: " + value); // 디버깅용 출력
            return defaultValue;  // 변환 실패 시 기본값 반환
        }
    }
}
