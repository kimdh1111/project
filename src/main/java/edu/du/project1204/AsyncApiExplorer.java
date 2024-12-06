package edu.du.project1204;

import org.json.JSONObject;
import org.json.XML;
import edu.du.project1204.ApiToDatabase;  // ApiToDatabase 클래스 임포트

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class AsyncApiExplorer {

    // 서비스 키와 URL을 준비합니다.
    private static final String serviceKey = "RxxiBoZCkNUsewp3STTp%2BfsoR%2BkNcbIaYYztjQYuLi3aHYRw1uhZ36bKYce%2FfWqeG8ST4NJA1IbRCdssJPOXKg%3D%3D";
    private static final String apiUrl = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/getHsptlMdcncListInfoInqire";

    public static void main(String[] args) {
        // 비동기 HTTP 요청을 통해 병상 정보를 가져옵니다.
        getHospitalDataAsync("서울특별시", "강남구", "삼성병원").thenAccept(response -> {
            if (response != null) {
                System.out.println("병원 정보:");
                System.out.println(response);
            } else {
                System.out.println("병원 정보 조회 실패");
            }
        });
    }

    // 비동기적으로 병원 데이터를 요청하는 메소드
    public static CompletableFuture<String> getHospitalDataAsync(String city, String district, String hospitalName) {
        // URL 생성
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("STAGE1","UTF-8") + "=" + URLEncoder.encode("서울특별시", "UTF-8")); /*주소(시도)*/
            urlBuilder.append("&" + URLEncoder.encode("STAGE2","UTF-8") + "=" + URLEncoder.encode("강남구", "UTF-8")); /*주소(시군구)*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*목록 건수*/



            // URL이 잘 만들어졌는지 확인하려면 다음과 같이 출력할 수 있습니다.
            System.out.println("API 호출 URL: " + urlBuilder.toString());


            // 이후 이 URL을 사용하여  비동기 HTTP 요청을 보내기 위한 HttpClient와 HttpRequest 사용
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Content-Type", "application/json")
                    .build();

            // 비동기적으로 HTTP 요청을 보내고 응답을 받기
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(AsyncApiExplorer::processResponse);  // 응답을 처리하는 메소드
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

    // HTTP 응답을 처리하고, XML을 JSON으로 변환하는 메소드
    private static String processResponse(String xmlResponse) {
        // 응답 출력 (디버깅용)
        System.out.println("API 응답 XML: " + xmlResponse);

        // 응답 내용에 따라 데이터가 없으면 메시지 반환
        if (xmlResponse.contains("<totalCount>0</totalCount>") || !xmlResponse.contains("<item>")) {
            return "데이터가 없습니다. 요청 조건을 확인하세요.";
        } else {
            // XML을 JSON으로 변환
            JSONObject jsonResponse = XML.toJSONObject(xmlResponse);
            String jsonData = jsonResponse.toString(4); // 예쁘게 JSON 출력 (들여쓰기 4칸)

            // 필요한 정보 추출 후 Hospital 객체로 변환하여 DB에 저장
            saveDataToDatabase(jsonData);
            return jsonData;
        }
    }

    // API 데이터를 DB에 저장하는 메소드
    private static void saveDataToDatabase(String jsonData) {
        // JSON 데이터를 파싱하여 Hospital 객체로 변환
        // 여기서는 예시로 JSON 형식에 맞춰 파싱하고, Hospital 객체를 DB에 저장하는 과정입니다.
        // 실제 JSON 구조에 맞게 파싱 로직을 작성합니다.

        // 예시 코드:
        // 1. jsonData를 파싱하여 Hospital 객체를 만들고,
        // 2. ApiToDatabase.saveHospitalToDatabase() 메서드를 호출하여 DB에 저장합니다.
        Hospital hospital = new Hospital();
        hospital.setName("병원 이름");
        hospital.setAddress("병원 주소");
        hospital.setPhoneNumber("병원 전화번호");
        hospital.setBedCount(100); // 예시로 병상 수 설정

        // DB에 저장
        try {
            ApiToDatabase.saveHospitalToDatabase(hospital);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
