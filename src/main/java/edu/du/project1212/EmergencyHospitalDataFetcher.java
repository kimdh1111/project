package edu.du.project1212;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.du.project1212.test.BedInfoService;
import edu.du.project1212.test.BedStatus;
import edu.du.project1212.test.BedStatusService;
import edu.du.project1212.test.ex.IcuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EmergencyHospitalDataFetcher {

    @Value("${api.key}")
    private String serviceKey;
    private static final int numOfRows = 206;

    private WebClient webClient;
    private EmergencyHospitalRepository hospitalRepository;

    @Autowired
    private BedInfoService bedInfoService;

    @Autowired
    private IcuInfoService icuInfoService;

    public EmergencyHospitalDataFetcher(WebClient webClient, EmergencyHospitalRepository hospitalRepository) {
        this.webClient = webClient;
        this.hospitalRepository = hospitalRepository;
    }

    public void fetchDataAndSave(String stage1, String stage2) {
        boolean hasMoreData = true;
        int currentPage = 1;

        while (hasMoreData) {
            int finalCurrentPage = currentPage;
            // 첫 번째 API 호출
            String response1 = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B552657/ErmctInfoInqireService/getEmrrmRltmUsefulSckbdInfoInqire") // 요청 경로
                            .queryParam("serviceKey", "{serviceKey}")
                            .queryParam("STAGE1", stage1)
                            .queryParam("STAGE2", stage2)
                            .queryParam("pageNo", finalCurrentPage)
                            .queryParam("numOfRows", numOfRows)
                            .build(serviceKey))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            // 두 번째 API 호출
            String response2 = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/B552657/ErmctInfoInqireService/getEgytListInfoInqire")
                            .queryParam("serviceKey", "{serviceKey}")
                            .build(serviceKey))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            try {
                XmlMapper xmlMapper = new XmlMapper();
                // 첫 번째 API 응답 처리
                JsonNode jsonResponse1 = xmlMapper.readTree(response1);
                // 두 번째 API 응답 처리
                JsonNode jsonResponse2 = xmlMapper.readTree(response2);



                int totalCount = jsonResponse1.path("body").path("totalCount").asInt();
                int totalPages = (int) Math.ceil((double) totalCount / numOfRows);


                // 응답 전체 구조 출력 (응답 구조가 예상과 다른지 확인)
                System.out.println("API Response: " + jsonResponse1.toString());
                JsonNode itemsNode1 = jsonResponse1.path("body").path("items").path("item");
                JsonNode itemsNode2 = jsonResponse2.path("body").path("items").path("item");



                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<EmergencyHospitalInfo> hospitalList = new ArrayList<>();

                String callTime = formatter(new Date());

                // 첫 번째 API 데이터 처리 및 두 번째 API 데이터 결합
                if (itemsNode1.isArray() && itemsNode2.isArray()) {
                    for (JsonNode itemNode1 : itemsNode1) {
                        EmergencyHospitalInfo hospitalInfo = objectMapper.convertValue(itemNode1, EmergencyHospitalInfo.class);
                        hospitalInfo.setCalltime(callTime);

                        // 두 번째 API 데이터 결합
                        for (JsonNode itemNode2 : itemsNode2) {
                            if (hospitalInfo.getDutyName().equals(itemNode2.path("dutyName").asText())) {
                                hospitalInfo.setDutyAddr(itemNode2.path("dutyAddr").asText());
                                // 모든 일치하는 항목에 대해 `dutyAddr`을 결합하려면 `break` 없이 계속 진행
                            }
                        }

                        hospitalList.add(hospitalInfo);
                    }
                }

                System.out.println("Saving data: " + hospitalList.size() + " records.");
                // 데이터를 hospitalRepository에 저장
                hospitalRepository.saveAll(hospitalList);
                System.out.println("Data saved successfully! Total items: " + hospitalList.size());
                System.out.println("Page " + currentPage + " data saved successfully!");


                currentPage++;
                if (currentPage > totalPages) {
                    hasMoreData = false;
                }

            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
                hasMoreData = false;
            }
        }

        System.out.println("All data has been retrieved and saved!");
    }

    private String formatter(Date date) {
        // 날짜를 원하는 형식으로 포맷하는 코드 작성 (예: yyyy-MM-dd HH:mm:ss)
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }



    @Scheduled(fixedRate = 600000)
    public void logging() {
//        deleteAll();
        fetchAllHospitalData();
    }

    private void fetchAllHospitalData() {
    }
}

