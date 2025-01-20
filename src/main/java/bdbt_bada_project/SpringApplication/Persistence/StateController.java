package bdbt_bada_project.SpringApplication.Persistence;

import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/api")
public class StateController {

    private final Map<Integer, StudentData> userStudentData = new HashMap<>();
    private final ScheduledExecutorService scheduler;
    private final SessionManager sessionManager; // Wstrzyknięcie SessionManager
    private List<CourseEntity> serverCourses;

    public StateController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        serverCourses = FAKE_DATA.generateCourses(20);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        for (int i = 1; i <= 6; i++) {
            userStudentData.put(i, FAKE_DATA.createExampleStudent());
        }
    }

    @PostMapping("/student/update")
    public String updateStudentDataOnServer(@RequestParam int userId, @RequestBody StudentData studentData) {
        if (!sessionManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        StudentData currentData = userStudentData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }

        if (studentData.getFirstName() != null) {
            currentData.setFirstName(studentData.getFirstName());
        }
        if (studentData.getLastName() != null) {
            currentData.setLastName(studentData.getLastName());
        }
        if (studentData.getEmail() != null) {
            currentData.setEmail(studentData.getEmail());
        }
        if (studentData.getPhoneNumber() != null) {
            currentData.setPhoneNumber(studentData.getPhoneNumber());
        }

        return "Student data updated successfully.";
    }

    @GetMapping("/student/data")
    public Object getStudentData(@RequestParam int userId) {
        if (!sessionManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        StudentData currentData = userStudentData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }
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

        if (!sessionManager.isSessionActive(userId)) {
            System.out.println("Session not active for userId: " + userId);
            return "Session not active. Please log in.";
        }

        StudentData currentData = userStudentData.get(userId);
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

        // Konwersja courseId na liczbę całkowitą
        int courseIdInt;
        try {
            courseIdInt = Integer.parseInt(courseId);
            System.out.println("Course ID parsed successfully: " + courseIdInt);
        } catch (NumberFormatException e) {
            System.out.println("Course ID is not a valid integer: " + courseId);
            return "Course ID is not a valid integer.";
        }

        // Sprawdzenie aktywności sesji
        if (!sessionManager.isSessionActive(userId)) {
            System.out.println("Session not active for userId: " + userId);
            return "Session not active. Please log in.";
        }

        // Pobranie danych użytkownika
        StudentData currentData = userStudentData.get(userId);
        if (currentData == null) {
            System.out.println("No data found for userId: " + userId);
            return "No data found for the given user ID.";
        }

        // Sprawdzenie czy kurs istnieje
        CourseEntity selectedCourse = serverCourses.stream()
                .filter(course -> course.getId() == courseIdInt)
                .findFirst()
                .orElse(null);

        if (selectedCourse == null) {
            System.out.println("Course with ID " + courseIdInt + " not found.");
            return "Course not found.";
        }

        // Dodanie kursu do listy zapisów użytkownika
        boolean added = currentData.addNewEnrollment(selectedCourse);
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
        if (!sessionManager.isSessionActive(userId)) {
            return "Session not active. Please log in.";
        }

        // Pobranie danych użytkownika
        StudentData currentData = userStudentData.get(userId);
        if (currentData == null) {
            return "No data found for the given user ID.";
        }

        // Pobranie ID kursów, na które użytkownik jest już zapisany
        List<Integer> enrolledCourseIds = currentData.getEnrollments().stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        // Filtrowanie kursów, na które użytkownik nie jest zapisany
        List<CourseEntity> availableCourses = serverCourses.stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .toList();

        return availableCourses;
    }

}
