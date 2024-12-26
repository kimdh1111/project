package edu.du.project1212.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")  // Vue.js 앱에서 CORS 요청을 허용
@RequestMapping("/api/bedinfo")  // /api/bedinfo로 요청을 받음
@RestController
public class BedInfoController {
    @Autowired
    private BedInfoService bedInfoService;
    @Autowired
    private BedInfoRepository bedInfoRepository;

    // /api/bedinfo 경로로 GET 요청을 처리하고 병상 정보를 반환
    @GetMapping  // 여기서 @GetMapping을 사용하면 /api/bedinfo 경로로 GET 요청을 받음
    public List<BedInfo> getBedInfo() {

        return bedInfoRepository.findAll();

    }
    // 병상 정보를 싱크하는 API
    @GetMapping("/syncbedinfo")  // 경로 수정
    public String syncBedInfo() {
        System.out.println("Syncing BedInfo data...");
        bedInfoService.syncBedInfo();
        return "BedInfo data synced successfully!";
    }
}
