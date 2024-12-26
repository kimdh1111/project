package edu.du.project1212.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChartController {
    @Autowired
    private BedInfoRepository bedInfoRepository;

    @GetMapping("/chart")
    public String showChart(Model model) {
        List<BedInfo> bedInfoList = bedInfoRepository.findAll();
        model.addAttribute("bedInfoList", bedInfoList);
        return "chart";  // 차트를 표시할 Thymeleaf 템플릿으로 이동
    }
}
