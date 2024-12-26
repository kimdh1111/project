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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence")
    @SequenceGenerator(name = "my_sequence", sequenceName = "MY_ENTITY_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "dutyName", nullable = false)//o
    private String dutyName; // 병원명

    @Column(name = "dutyAddr")
    private String  dutyAddr;

    @Column(name = "duty_tel3")//o
    private String dutyTel3; // 병원 전화번호

    @Column(name = "hpid", nullable = false)//o
    private String hpid; // 병원 ID

//    @Column(name = "hv2")
//    private String hv2; // 뇌출혈 가능 여부

    @Column(name = "hv1")
    private String hv1; // 뇌출혈 가능 여부

    @Column(name = "hv29")
    private String hv29; // 뇌경색 가능 여부

    @Column(name = "hv30")
    private String hv30; // 심혈관질환 가능 여부

    @Column(name = "hv40")
    private String hv40; // 흉부손상 가능 여부

    @Column(name = "hv5")
    private String hv5; // 신경외과 가능 여부

    @Column(name = "hv7")
    private String hv7; // 정형외과 가능 여부

    @Column(name = "hvamyn")
    private String hvamyn; // 응급실 가능 여부


    @Column(name = "hvec")
    private String hvec; // 응급의학과 전문의 수

    @Column(name = "hvgc")
    private String hvgc; // 일반 병상 수


    @Column(name = "hvidate")
    private String hvidate; // 정보 업데이트 날짜/시간


    @Column(name = "hvoc")
    private String hvoc; // 수술실 병상 수


    @Column(name = "hvs01")
    private String hvs01; // 응급실 총 병상 수

    @Column(name = "hvs03")
    private String hvs03; // 소아과 병상 수

    @Column(name = "hvs04")
    private String hvs04; // 내과 병상 수



    @Column(name = "hvs17")
    private String hvs17; // 외상센터 병상 수



    @Column(name = "hvs24")
    private String hvs24; // 외과 병상 수

    @Column(name = "hvs27")
    private String hvs27; // 흉부외과 병상 수

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


    @Column(name = "hv17")
    private String hv17;

    @Column(name = "hv18")
    private String hv18;

    @Column(name = "hv19")
    private String hv19;






//중환자

    //내과
    @Column(name = "hv2")
    private String hv2;

    //내과기준
    @Column(name = "hvs06")
    private String hvs06;

    //외과
    @Column(name = "hv3")
    private String hv3;

    //외과기준
    @Column(name = "hvs07")
    private String hvs07;

    //외상
    @Column(name = "hv9")
    private String hv9;

    //외상기준
    @Column(name = "hvs14")
    private String hvs14;

    //소아
    @Column(name = "hv32")
    private String hv32;

    //소아기준
    @Column(name = "hvs09")
    private String hvs09;

    //심장내과
    @Column(name = "hv34")
    private String hv34;

    //심장내과기준
    @Column(name = "hvs15")
    private String hvs15;

    //음압격리
    @Column(name = "hv35")
    private String hv35;

    //음압격리기준
    @Column(name = "hvs18")
    private String hvs18;





    @Column(name = "hvcc")
    private String hvcc;

    @Column(name = "hvncc")
    private String hvncc;

    @Column(name = "hvccc")
    private String hvccc;


    @Column(name = "hvicc")
    private String hvicc;



    @Column(name = "hv28")
    private String hv28;

    @Column(name = "hv42")
    private String hv42;


    @Column(name = "hvs02")
    private String hvs02;

    @Column(name = "hvs26")
    private String hvs26;
}