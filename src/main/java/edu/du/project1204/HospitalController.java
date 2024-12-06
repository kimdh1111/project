package edu.du.project1204;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class HospitalController {

    @GetMapping("/hospitals")
    public String getHospitals(Model model) throws SQLException {
        // DB에서 병원 정보 가져오기
        List<Hospital> hospitals = ApiToDatabase.getHospitalsFromDatabase();  // DB에서 병원 정보 가져오기
        System.out.println("Fetched Hospitals: " + hospitals.size());  // 병원 목록 사이즈 확인
        model.addAttribute("hospitals", hospitals);  // Thymeleaf 템플릿에 전달
        return "hospital_list";  // hospital_list.html을 반환
    }
}
