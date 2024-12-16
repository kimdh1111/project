package edu.du.project1212;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.du.project1212.test.BedInfo;
import edu.du.project1212.test.BedInfoRepository;
import edu.du.project1212.test.BedInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class EmergencyHospitalDataFetcher {


    private static final String serviceKey = "";
    private static final int numOfRows = 206;

    private WebClient webClient;
    private EmergencyHospitalRepository hospitalRepository;

    @Autowired
    private BedInfoService bedInfoService;

    public EmergencyHospitalDataFetcher(WebClient webClient, EmergencyHospitalRepository hospitalRepository) {
        this.webClient = webClient;
        this.hospitalRepository = hospitalRepository;
    }

    public void fetchDataAndSave(String stage1, String stage2) {
        boolean hasMoreData = true;
        int currentPage = 1;

        while (hasMoreData) {
            int finalCurrentPage = currentPage;

            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("") // 요청 경로
                            .queryParam("serviceKey", "{serviceKey}")
                            .queryParam("STAGE1", stage1)
                            .queryParam("STAGE2", stage2)
                            .queryParam("pageNo", finalCurrentPage)
                            .queryParam("numOfRows", numOfRows)
                            .build(serviceKey))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            try {
                XmlMapper xmlMapper = new XmlMapper();
                JsonNode jsonResponse = xmlMapper.readTree(response);

                int totalCount = jsonResponse.path("body").path("totalCount").asInt();
                int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

                // 응답 전체 구조 출력 (응답 구조가 예상과 다른지 확인)
                System.out.println("API Response: " + jsonResponse.toString());
                JsonNode itemsNode = jsonResponse.path("body").path("items").path("item");

                ObjectMapper objectMapper = new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<EmergencyHospitalInfo> hospitalList = new ArrayList<>();

                String callTime = formatter(new Date());

                if (itemsNode.isArray()) {
                    for (JsonNode itemNode : itemsNode) {
                        EmergencyHospitalInfo hospitalInfo = objectMapper.convertValue(itemNode, EmergencyHospitalInfo.class);
                        hospitalInfo.setCalltime(callTime);
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

