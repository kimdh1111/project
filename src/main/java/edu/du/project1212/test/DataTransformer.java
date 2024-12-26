package edu.du.project1212.test;

import edu.du.project1212.EmergencyHospitalInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataTransformer {
    @Autowired
    private BedInfoRepository bedInfoRepository;

    public void transformAndSaveData(List<EmergencyHospitalInfo> hospitalList) {
        for (EmergencyHospitalInfo hospitalInfo : hospitalList) {
            BedInfo bedInfo = new BedInfo();

            // 병상 유형 설정 (예시: 응급실 총 병상 수 -> "응급실")
            bedInfo.setBedType("응급실");  // 여기서는 하드코딩, 필요시 다른 로직으로 처리 가능

            // 사용 가능 병상 수와 사용 중 병상 수 설정
            bedInfo.setAvailableBeds(parseIntOrZero(hospitalInfo.getHvs01()));  // 예시: 응급실 총 병상 수
            bedInfo.setUsedBeds(parseIntOrZero(hospitalInfo.getHvs03()));  // 예시: 소아과 병상 수

            // 중증도 설정 (예시: 병원의 특성에 맞춰 세부 분류가 필요할 수 있음)
            bedInfo.setSeverity("중증");  // 예시로 '중증'으로 설정. 필요에 맞게 로직을 추가

            // BedInfo 저장
            bedInfoRepository.save(bedInfo);
        }
    }

    private int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;  // 숫자가 아닐 경우 0으로 반환
        }
    }
}
