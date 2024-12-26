package edu.du.project1212.test;

import edu.du.project1212.EmergencyHospitalInfo;
import edu.du.project1212.EmergencyHospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BedInfoService {

    @Autowired
    private BedInfoRepository bedInfoRepository;

    @Autowired
    private EmergencyHospitalRepository emergencyHospitalRepository;

    @Transactional
    public void syncBedInfo() {
        // EmergencyHospitalInfo 테이블에서 병원 데이터를 가져옵니다.
        List<EmergencyHospitalInfo> hospitalData = emergencyHospitalRepository.findAll();
        System.out.println("Number of hospitals: " + hospitalData.size());  // 병원 데이터 수 출력

        // 병원 데이터를 BedInfo로 변환하여 저장
        List<BedInfo> bedInfoList = new ArrayList<>();
        for (EmergencyHospitalInfo hospital : hospitalData) {
            // 예시: 병상 종류별로 병상 수를 저장하고 중증도 설정
            addBedInfo(bedInfoList, "응급실", hospital.getHvs01(), hospital.getHvs01(), "Critical", hospital.getCalltime());
            addBedInfo(bedInfoList, "소아과", hospital.getHvs03(), hospital.getHvs03(), "Moderate", hospital.getCalltime());
            addBedInfo(bedInfoList, "내과", hospital.getHvs04(), hospital.getHvs04(), "Mild", hospital.getCalltime());

            // 여기에 추가적인 병상 종류를 처리하는 코드 작성
        }

        // bedInfoList가 비어 있지 않음을 확인
        System.out.println("BedInfo List size before saving: " + bedInfoList.size());  // 리스트 크기 출력

        if (!bedInfoList.isEmpty()) {
            // 데이터를 DB에 저장
            bedInfoRepository.saveAll(bedInfoList);
            bedInfoRepository.flush();  // 즉시 DB에 반영
            System.out.println("BedInfo data saved successfully.");
        } else {
            System.out.println("No BedInfo data to save.");
        }
    }

    private void addBedInfo(List<BedInfo> bedInfoList, String bedType, String availableBeds, String usedBeds, String severity, String callTime) {
        BedInfo bedInfo = new BedInfo();
        bedInfo.setBedType(bedType);

        // 값 출력 (디버깅용)
        System.out.println("Adding BedInfo: " + bedType + ", Available: " + availableBeds + ", Used: " + usedBeds);

        // 값 변환 후 출력 (디버깅용)
        int available = parseIntOrDefault(availableBeds, 0);
        int used = parseIntOrDefault(usedBeds, 0);
        System.out.println("Converted Available: " + available + ", Converted Used: " + used);

        bedInfo.setAvailableBeds(available);  // 숫자로 변환하여 저장
        bedInfo.setUsedBeds(used);  // 숫자로 변환하여 저장
        bedInfo.setSeverity(severity);  // 중증도 설정
        // EmergencyHospitalInfo에서 가져온 calltime을 그대로 저장
        bedInfo.setCalltime(callTime);  // calltime을 그대로 String으로 저장
        bedInfoList.add(bedInfo);
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