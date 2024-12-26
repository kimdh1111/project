package edu.du.project1212.test.ex;

import edu.du.project1212.EmergencyHospitalInfo;
import edu.du.project1212.EmergencyHospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class IcuInfoService {
    @Autowired
    private IcuInfoRepository icuInfoRepository;  // IcuInfoRepository로 변경

    @Autowired
    private EmergencyHospitalRepository emergencyHospitalRepository;

    @Transactional
    public void syncIcuInfo() {  // syncIcuInfo로 메서드명 변경
        // EmergencyHospitalInfo 테이블에서 병원 데이터를 가져옵니다.
        List<EmergencyHospitalInfo> hospitalData = emergencyHospitalRepository.findAll();
        System.out.println("Number of hospitals: " + hospitalData.size());  // 병원 데이터 수 출력

        // 병원 데이터를 IcuInfo로 변환하여 저장
        List<IcuInfo> icuInfoList = new ArrayList<>();  // IcuInfo로 타입 변경
        for (EmergencyHospitalInfo hospital : hospitalData) {
            // 예시: 병상 종류별로 병상 수를 저장하고 중증도 설정
            addIcuInfo(icuInfoList, "일반", hospital.getHvec(), hospital.getHvs01(), hospital.getDutyName(), hospital.getHvidate(), hospital.getDutyAddr());
            addIcuInfo(icuInfoList, "소아", hospital.getHv28(), hospital.getHvs02(), hospital.getDutyName(), hospital.getHvidate(), hospital.getDutyAddr());
            addIcuInfo(icuInfoList, "응급실 음압 격리 병상", hospital.getHv29(), hospital.getHvs03(), hospital.getDutyName(), hospital.getHvidate(), hospital.getDutyAddr());
            addIcuInfo(icuInfoList, "응급실 일반 격리 병상", hospital.getHv30(), hospital.getHvs04(), hospital.getDutyName(), hospital.getHvidate(), hospital.getDutyAddr());
            addIcuInfo(icuInfoList, "분만실", hospital.getHv42(), hospital.getHvs26(), hospital.getDutyName(), hospital.getHvidate(), hospital.getDutyAddr());




            // 여기에 추가적인 병상 종류를 처리하는 코드 작성
        }

        // icuInfoList가 비어 있지 않음을 확인
        System.out.println("IcuInfo List size before saving: " + icuInfoList.size());  // 리스트 크기 출력

        if (!icuInfoList.isEmpty()) {
            // 데이터를 DB에 저장
            icuInfoRepository.saveAll(icuInfoList);
            icuInfoRepository.flush();  // 즉시 DB에 반영
            System.out.println("IcuInfo data saved successfully.");
        } else {
            System.out.println("No IcuInfo data to save.");
        }
    }

    private void addIcuInfo(List<IcuInfo> icuInfoList, String department, String usedBeds,  String totalBeds, String dutyName, String hvidate, String dutyAddr) {  // addIcuInfo로 메서드명 변경
        IcuInfo icuInfo = new IcuInfo();  // IcuInfo 객체 생성

        icuInfo.setDepartment(department);  // 병상 유형 설정


        int used = parseIntOrDefault(usedBeds, 0);  // 사용 중 병상 수
        int total = parseIntOrDefault(totalBeds, 0);
        int available = total - used;  // 사용 가능한 병상은 전체 병상에서 사용 중 병상을 뺀 값

        icuInfo.setDepartment(department);  // 중증도 설정
        icuInfo.setUsedBeds(used);  // 사용 중 병상 수 설정
        icuInfo.setTotalBeds(total);  // 기준 병상 수 설정
        icuInfo.setAvailableBeds(available);  // 사용 가능 병상 수 설정
        // EmergencyHospitalInfo에서 가져온 calltime을 그대로 저장
        icuInfo.setHvidate(hvidate);  // 입원날짜
        icuInfo.setDutyName(dutyName);  // 병원명 설정
        icuInfo.setDutyAddr(dutyAddr);  // 주소
        icuInfoList.add(icuInfo);  // 리스트에 추가
    }

    // 문자열을 정수로 변환하고, 변환이 불가능하거나 음수인 경우 0을 반환하는 메서드
    private int parseIntOrDefault(String value, int defaultValue) {

        // 만약 value가 null이거나 빈 문자열이면 기본값을 반환
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;  // 빈 문자열이나 null인 경우 기본값을 반환
        }

        try {
            // 문자열을 정수로 변환
            int parsedValue = Integer.parseInt(value);  // 문자열을 정수로 변환하여 parsedValue에 저장

            // 만약 변환된 값이 음수이면 0을 반환
            return parsedValue < 0 ? 0 : parsedValue;  // 음수일 경우 0 반환, 그 외에는 변환된 값 반환
        } catch (NumberFormatException e) {
            // 정수로 변환할 수 없는 값이 들어오면 예외 발생
            System.out.println("Error parsing value: " + value); // 디버깅용: 변환 실패한 값을 출력
            return defaultValue;  // 변환 실패 시 기본값을 반환
        }
    }

}
