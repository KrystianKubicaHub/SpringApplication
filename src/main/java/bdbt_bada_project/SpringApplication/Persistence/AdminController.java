package bdbt_bada_project.SpringApplication.Persistence;

import bdbt_bada_project.SpringApplication.entities.AcademyEntity;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GlobalDataManager globalDataManager;

    public AdminController(GlobalDataManager globalDataManager) {
        this.globalDataManager = globalDataManager;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        List<CourseEntity> courses = globalDataManager.serverCourses;
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/courses/update")
    public ResponseEntity<List<CourseEntity>> getUpdatedCourses() {
        List<CourseEntity> updatedCourses = globalDataManager.serverCourses;

        ///  tu będzie problem
        return ResponseEntity.ok(updatedCourses);
    }
    @PostMapping("/courses/add")
    public ResponseEntity<String> addCourse(@RequestBody CourseEntity newCourse) {
        if (newCourse == null || newCourse.getName() == null || newCourse.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid course data. Name is required.");
        }
        globalDataManager.serverCourses.add(newCourse);

        return ResponseEntity.ok("Course added successfully.");
    }

    @PostMapping("/users/add")
    public ResponseEntity<String> addUser(@RequestBody NewUserRequest newUserData) {
        if (newUserData == null || newUserData.getLogin() == null || newUserData.getPassword() == null || newUserData.getRole() == null) {
            return ResponseEntity.badRequest().body("Invalid user data. All fields are required.");
        }

        if (newUserData.getRole() == UserSessionController.UserRole.STUDENT) {
            if (newUserData.getStudentData() == null) {
                return ResponseEntity.badRequest().body("Student data is required for role STUDENT.");
            }
            globalDataManager.userStudentData.put(newUserData.getStudentData().getId(), newUserData.getStudentData());
        }

        UserSessionController.UserAccount newAccount = new UserSessionController.UserAccount(
                newUserData.getRole() == UserSessionController.UserRole.STUDENT ? newUserData.getStudentData().getId() : generateNewId(),
                newUserData.getLogin(),
                newUserData.getPassword(),
                newUserData.getRole()
        );

        globalDataManager.userAccounts.add(newAccount);

        return ResponseEntity.ok("User added successfully.");
    }


    public static class NewUserRequest {
        private String login;
        private String password;
        private UserSessionController.UserRole role;
        private StudentData studentData;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public UserSessionController.UserRole getRole() {
            return role;
        }

        public void setRole(UserSessionController.UserRole role) {
            this.role = role;
        }

        public StudentData getStudentData() {
            return studentData;
        }

        public void setStudentData(StudentData studentData) {
            this.studentData = studentData;
        }
    }

    private int generateNewId() {
        return globalDataManager.userAccounts.stream()
                .mapToInt(UserSessionController.UserAccount::getId)
                .max()
                .orElse(0) + 1;
    }

    @GetMapping("/academy/info")
    public ResponseEntity<Object> getAcademyInfo() {
        AcademyEntity academyEntity = globalDataManager.academyEntity;

        if (academyEntity != null) {
            return ResponseEntity.ok(academyEntity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }
    }
    @PostMapping("/notify-change")
    public ResponseEntity<String> notifyAcademyChange(@RequestBody String targetUrl) {
        AcademyEntity academyEntity = globalDataManager.academyEntity;
        if (academyEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForObject(targetUrl, academyEntity, String.class);
            return ResponseEntity.ok("Change notification sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification: " + e.getMessage());
        }
    }




}
