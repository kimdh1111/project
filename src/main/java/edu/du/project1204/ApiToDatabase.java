package edu.du.project1204;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import edu.du.project1204.ApiToDatabase;  // 올바르게 임포트되어야 함

public class ApiToDatabase {

    // 데이터베이스에서 병원 정보를 가져오는 메서드
    public static List<Hospital> getHospitalsFromDatabase() throws SQLException {
        List<Hospital> hospitals = new ArrayList<>();

        String jdbcUrl = "jdbc:mysql://localhost:3307/HospitalDB"; // DB URL
        String dbUsername = "root";  // MySQL 사용자명
        String dbPassword = "mysql";  // MySQL 비밀번호

        // DB 연결
        Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
        String sql = "SELECT * FROM HospitalInfo";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // 병원 정보 리스트에 추가
        while (rs.next()) {
            Hospital hospital = new Hospital();
            hospital.setName(rs.getString("name"));
            hospital.setAddress(rs.getString("address"));
            hospital.setPhoneNumber(rs.getString("phone_number"));
            hospitals.add(hospital);
        }

        // 연결 종료
        rs.close();
        stmt.close();
        conn.close();

        return hospitals;
    }


    // 병원 데이터를 DB에 저장하는 메서드
    public static void saveHospitalToDatabase(Hospital hospital) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3307/HospitalDB";
        String dbUsername = "root";
        String dbPassword = "mysql";

        // DB 연결
        Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        String sql = "INSERT INTO HospitalInfo (name, address, phone_number, bed_count) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, hospital.getName());
        pstmt.setString(2, hospital.getAddress());
        pstmt.setString(3, hospital.getPhoneNumber());
        pstmt.setInt(4, hospital.getBedCount());
        pstmt.executeUpdate();

        pstmt.close();
        conn.close();
    }
}
