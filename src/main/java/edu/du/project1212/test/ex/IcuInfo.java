package edu.du.project1212.test.ex;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class IcuInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence")
    @SequenceGenerator(name = "my_sequence", sequenceName = "MY_ENTITY_SEQ", allocationSize = 1)
    private Long id; // 각 병상 ID (Primary Key)

    @Column(nullable = false)
    private String department; // 과목 (ex: 소아, 외상, 외과 등)

    private int availableBeds;  // 사용 가능 병상 수
    private int usedBeds;  // 사용 중 병상 수
    private int totalBeds; // 전체 병상수 (기준)



    @Column(name = "dutyName")//병원이름
    private String  dutyName;

    @Column(name = "hvidate")//입원일시
    private String  hvidate;

    @Column(name = "dutyAddr")//주소
    private String  dutyAddr;

}
