package edu.du.project1212.test;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class BedInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence")
    @SequenceGenerator(name = "my_sequence", sequenceName = "MY_ENTITY_SEQ", allocationSize = 1)
    private Long id; // 각 병상 ID (Primary Key)

    @Column(nullable = false)
    private String bedType;  // 병상 유형

    private int availableBeds;  // 사용 가능 병상 수
    private int usedBeds;  // 사용 중 병상 수

    private String severity;  // 중증도

    @Column(name= "calltime")
    private String calltime;

}
