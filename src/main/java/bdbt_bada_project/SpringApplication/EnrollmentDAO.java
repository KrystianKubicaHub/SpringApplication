package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    /*
    private final String url = "jdbc:oracle:thin:@localhost:1522:xe"; // Poprawiony URL
    private final String username = "SYSTEM"; // Zmień, jeśli używasz innego użytkownika
    private final String password = "#4_9YiMXQ9gQU9!";

    public List<EnrollmentEntity> getAllEnrollments() {
        List<EnrollmentEntity> enrollments = new ArrayList<>();
        String query = "SELECT Kurs_Id, Student_Czlowiek_Id, Data_zapisu FROM Zapisy";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                EnrollmentEntity enrollment = new EnrollmentEntity();
                enrollment.setCourse(resultSet.getInt("Kurs_Id"));
                enrollment.setStudentId(resultSet.getInt("Student_Czlowiek_Id"));
                enrollment.setEnrollmentDate(resultSet.getDate("Data_zapisu"));
                enrollments.add(enrollment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return enrollments;
    }

     */
}
