package edu.du.project1212.test;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class BedStatus {
    @Id
    private Long id;

    @Column(nullable = false)
    private String status;  // 병상 상태 (음압격리, 일반격리 등)

    @Column(nullable = false)
    private String bedType;  // 병상 유형

    private int availableBeds;  // 사용 가능 병상 수
    private int usedBeds;  // 사용 중 병상 수
}
