package edu.du.project1212.test;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BedInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // id 자동 증가
    private Long id; // 각 병상 ID (Primary Key)

    @Column(nullable = false)
    private String bedType;  // 병상 유형

    private int availableBeds;  // 사용 가능 병상 수
    private int usedBeds;  // 사용 중 병상 수

    private String severity;  // 중증도
}
