package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.Helpers.ServerGUI;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.SQLCoincidence.SQLService;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public void testAddCourse() {
		/*
		System.out.println("===== TESTING addCourse() AT STARTUP =====");

		CourseEntity testCourse = new CourseEntity(
				null,
				"Test Course",
				"This is a test course to check DB insertion.",
				5,
				null
		);

		try {
			SQLService service = new SQLService()
			boolean result = SQLService.addCourse(testCourse);


			if (result) {
				System.out.println("✅ addCourse() TEST PASSED: Course added successfully.");
			} else {
				System.err.println("❌ addCourse() TEST FAILED: Course insertion failed.");
			}
		} catch (Exception e) {
			System.err.println("❌ ERROR in addCourse(): " + e.getMessage());
			e.printStackTrace(); // Pełny błąd w logach
		}

		 */
	}
}
