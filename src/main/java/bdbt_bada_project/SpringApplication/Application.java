package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.DataModels.StateController;
import bdbt_bada_project.SpringApplication.DataModels.StudentData;
import bdbt_bada_project.SpringApplication.entities.PersonEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
		///EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
		///List<EnrollmentEntity> enrollments = enrollmentDAO.getAllEnrollments();

		//testSerialization();
	}



}
