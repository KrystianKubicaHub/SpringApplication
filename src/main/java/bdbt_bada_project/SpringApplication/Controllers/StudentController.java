package bdbt_bada_project.SpringApplication.Controllers;

import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.SQLCoincidence.SQLService;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {


    private final GlobalDataManager globalDataManager;
    private final SQLService sqlService;


    public StudentController(GlobalDataManager globalDataManager, SQLService sqlService) {
        this.globalDataManager = globalDataManager;
        this.sqlService = sqlService;
    }

    @PostMapping("/student/update")
    public String updateStudentDataOnServer(@RequestParam int userId, @RequestBody StudentData studentData) {
        if (!globalDataManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        StudentData currentData = globalDataManager.studentsData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }

        if (studentData.getFirstName() != null) {
            currentData.setFirstName(studentData.getFirstName());
            sqlService.updateFirstName(studentData.id, studentData.firstName);
        }
        if (studentData.getLastName() != null) {
            currentData.setLastName(studentData.getLastName());
            sqlService.updateLastName(studentData.id, studentData.lastName);
        }
        if (studentData.getEmail() != null) {
            currentData.setEmail(studentData.getEmail());
            sqlService.updateEmail(studentData.id, studentData.email);
        }
        if (studentData.getPhoneNumber() != null) {
            currentData.setPhoneNumber(studentData.getPhoneNumber());
            sqlService.updateLastName(studentData.id, studentData.phoneNumber);
        }

        return "Student data updated successfully.";
    }

    @GetMapping("/student/data")
    public Object getStudentData(@RequestParam int userId) {
        if (!globalDataManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        StudentData currentData = globalDataManager.studentsData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }
        System.out.println(currentData.getEnrollments().get(3));
        return currentData;
    }

    @PostMapping("/enrollment/remove")
    public String removeEnrollmentById(@RequestParam int userId, @RequestBody String enrollmentId) {
        System.out.println("Próba usunięcia enrollmentu: userId = " + userId + ", enrollmentId = " + enrollmentId);

        int enrollmentIdInt;
        try {
            enrollmentIdInt = Integer.parseInt(enrollmentId);
            System.out.println("Enrollment ID parsed successfully: " + enrollmentIdInt);
        } catch (NumberFormatException e) {
            System.out.println("Enrollment ID is not a valid integer: " + enrollmentId);
            return "Enrollment ID is not a valid integer.";
        }

        if (!globalDataManager.isSessionActive(userId)) {
            System.out.println("Session not active for userId: " + userId);
            return "Session not active. Please log in.";
        }

        StudentData currentData = globalDataManager.studentsData.get(userId);
        if (currentData == null) {
            System.out.println("No data found for userId: " + userId);
            return "No data found for the given user ID.";
        }

        boolean removed = currentData.getEnrollments()
                .removeIf(enrollment -> enrollment.getId() == enrollmentIdInt);

        if (removed) {
            System.out.println("Enrollment with ID " + enrollmentIdInt + " removed successfully for userId: " + userId);
            return "Enrollment removed successfully.";
        } else {
            System.out.println("Enrollment with ID " + enrollmentIdInt + " not found for userId: " + userId);
            return "Enrollment not found.";
        }
    }

    @PostMapping("/courses/register")
    public String registerForCourse(@RequestParam int userId, @RequestBody String courseId) {
        System.out.println("Próba zapisu na kurs: userId = " + userId + ", courseId = " + courseId);


        int courseIdInt;
        try {
            courseIdInt = Integer.parseInt(courseId);
            System.out.println("Course ID parsed successfully: " + courseIdInt);
        } catch (NumberFormatException e) {
            System.out.println("Course ID is not a valid integer: " + courseId);
            return "Course ID is not a valid integer.";
        }

        if (!globalDataManager.isSessionActive(userId)) {
            System.out.println("Session not active for userId: " + userId);
            return "Session not active. Please log in.";
        }

        StudentData currentData = globalDataManager.studentsData.get(userId);
        if (currentData == null) {
            System.out.println("No data found for userId: " + userId);
            return "No data found for the given user ID.";
        }

        CourseEntity selectedCourse = this.globalDataManager.academyEntity.getEntityCourses().stream()
                .filter(course -> course.getId() == courseIdInt)
                .findFirst()
                .orElse(null);

        if (selectedCourse == null) {
            System.out.println("Course with ID " + courseIdInt + " not found.");
            return "Course not found.";
        }

        int enrollemnt_id = sqlService.addEnrollment(userId, courseId);
        boolean added = currentData.addNewEnrollment(selectedCourse, enrollemnt_id);
        if (added) {
            System.out.println("User with ID " + userId + " successfully registered for course: " + selectedCourse.getName());
            return "Successfully registered for course: " + selectedCourse.getName();
        } else {
            System.out.println("User with ID " + userId + " is already enrolled in course: " + selectedCourse.getName());
            return "You are already enrolled in this course.";
        }
    }


    @GetMapping("/courses")
    public Object getServerCourses(@RequestParam int userId) {
        if (!globalDataManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        // Pobranie danych użytkownika
        StudentData currentData = globalDataManager.studentsData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }

        // Pobranie ID kursów, na które użytkownik jest już zapisany
        List<Integer> enrolledCourseIds = currentData.getEnrollments().stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        // Filtrowanie kursów, na które użytkownik nie jest zapisany
        List<CourseEntity> availableCourses = this.globalDataManager.academyEntity.getEntityCourses().stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .toList();

        return availableCourses;
    }

}
