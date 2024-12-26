package edu.du.project1212.test;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class EmergencyContact {
    @Id
    private Long id;

    @Column(nullable = false)
    private String bedType;  // 병상 유형

    private String emergencyPhone;  // 응급실 연락처
}
