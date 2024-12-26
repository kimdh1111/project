package edu.du.project1212;

import edu.du.project1212.test.BedInfoService;
import edu.du.project1212.test.BedStatusService;
import edu.du.project1212.test.ex.IcuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EmergencyHospitalDataFetcherApp implements CommandLineRunner {
    @Autowired
    private EmergencyHospitalDataFetcher dataFetcher;


    @Autowired
    private BedInfoService bedInfoService; // BedInfoService를 주입받습니다.
    @Autowired
    private BedStatusService bedStatusService;
    @Autowired
    private IcuInfoService icuInfoService;
    public static void main(String[] args) {
        SpringApplication.run(EmergencyHospitalDataFetcherApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 예시로 서울특별시와 강남구에 대한 데이터를 수집하고 저장
        dataFetcher.fetchDataAndSave("서울특별시", "" );

        // BedInfo 동기화
        bedInfoService.syncBedInfo(); // 이 부분이 추가되어야 합니다.
        bedStatusService.syncBedStatus();
        icuInfoService.syncIcuInfo(); // 이 부분이 추가되어야 합니다.
        System.out.println("BedInfo sync complete!");
    }


}
