package edu.du.project1212.test.ex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")  // Vue.js 앱에서 CORS 요청을 허용
@RequestMapping("/api/icuinfo")  // /api/bedinfo로 요청을 받음
@RestController
public class IcuInfoController {
    @Autowired
    private IcuInfoService icuInfoService;
    @Autowired
    private IcuInfoRepository icuInfoRepository;

    // /api/bedinfo 경로로 GET 요청을 처리하고 병상 정보를 반환
    @GetMapping  // 여기서 @GetMapping을 사용하면 /api/bedinfo 경로로 GET 요청을 받음
    public List<IcuInfo> getIcuInfo() {

        return icuInfoRepository.findAll();

    }
    // 병상 정보를 싱크하는 API
    @GetMapping("/syncicuinfo")  // 경로 수정
    public String syncIcuInfo() {
        System.out.println("Syncing BedInfo data...");
        icuInfoService.syncIcuInfo();
        return "BedInfo data synced successfully!";
    }
}
