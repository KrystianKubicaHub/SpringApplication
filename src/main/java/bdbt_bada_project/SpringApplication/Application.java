package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.DataModels.StudentData;
import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		//EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
		///List<EnrollmentEntity> enrollments = enrollmentDAO.getAllEnrollments();

		StudentData studentData = StudentData.getInstance();
		System.out.println(studentData);
		System.out.println("Enrollments for student:");
		for (EnrollmentEntity enrollment : studentData.getEnrollments()) {
			//System.out.println(enrollment);
		}
	}

}
