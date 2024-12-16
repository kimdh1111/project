package edu.du.project1212;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "emergency_hospital_info",uniqueConstraints = @UniqueConstraint(columnNames = {"hpid", "calltime"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyHospitalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duty_name", nullable = false)//o
    private String dutyName; // 병원명

    @Column(name = "duty_tel3")//o
    private String dutyTel3; // 병원 전화번호

    @Column(name = "hpid", nullable = false)//o
    private String hpid; // 병원 ID

    @Column(name = "hv2")
    private String hv2; // 뇌출혈 가능 여부

    @Column(name = "hv1")
    private String hv1; // 뇌출혈 가능 여부

    @Column(name = "hv29")
    private String hv29; // 뇌경색 가능 여부

    @Column(name = "hv30")
    private String hv30; // 심혈관질환 가능 여부

    @Column(name = "hv35")
    private String hv35; // 복부손상 가능 여부

    @Column(name = "hv40")
    private String hv40; // 흉부손상 가능 여부

    @Column(name = "hv5")
    private String hv5; // 신경외과 가능 여부

    @Column(name = "hv7")
    private String hv7; // 정형외과 가능 여부

    @Column(name = "hvamyn")
    private String hvamyn; // 응급실 가능 여부

    @Column(name = "hvctayn")
    private String hvctayn; // CT 촬영 가능 여부

    @Column(name = "hvec")
    private String hvec; // 응급의학과 전문의 수

    @Column(name = "hvgc")
    private String hvgc; // 일반 병상 수

    @Column(name = "hvicc")
    private String hvicc; // 중환자실 병상 수

    @Column(name = "hvidate")
    private String hvidate; // 정보 업데이트 날짜/시간

    @Column(name = "hvincuayn")
    private String hvincuayn; // 신생아 중환자실 가능 여부

    @Column(name = "hvmriayn")
    private String hvmriayn; // MRI 촬영 가능 여부

    @Column(name = "hvoc")
    private String hvoc; // 수술실 병상 수

    @Column(name = "hvoxyayn")
    private String hvoxyayn; // 고압산소치료 가능 여부

    @Column(name = "hvs01")
    private String hvs01; // 응급실 총 병상 수

    @Column(name = "hvs03")
    private String hvs03; // 소아과 병상 수

    @Column(name = "hvs04")
    private String hvs04; // 내과 병상 수

    @Column(name = "hvs06")
    private String hvs06; // 정형외과 병상 수

    @Column(name = "hvs17")
    private String hvs17; // 외상센터 병상 수

    @Column(name = "hvs18")
    private String hvs18; // 신경외과 병상 수

    @Column(name = "hvs22")
    private String hvs22; // 심혈관 병상 수

    @Column(name = "hvs24")
    private String hvs24; // 외과 병상 수

    @Column(name = "hvs27")
    private String hvs27; // 흉부외과 병상 수

    @Column(name = "hvs28")
    private String hvs28; // 소화기내과 병상 수

    @Column(name = "hvs29")
    private String hvs29; // 간담췌 병상 수

    @Column(name = "hvs30")
    private String hvs30; // 기타 병상 수

    @Column(name = "hvs33")
    private String hvs33; // 신장내과 병상 수

    @Column(name = "hvs38")
    private String hvs38; // 기타 응급 병상 수

    @Column(name = "hvventiayn")
    private String hvventiayn; // 인공호흡기 가능 여부

    @Column(name = "hvventisoayn")
    private String hvventisoayn; // 단독 인공호흡기 가능 여부

    @Column(name= "calltime")
    private String calltime;
}