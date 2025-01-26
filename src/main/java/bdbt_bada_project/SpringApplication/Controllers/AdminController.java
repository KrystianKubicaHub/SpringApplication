package bdbt_bada_project.SpringApplication.Controllers;

import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GlobalDataManager globalDataManager;

    public AdminController(GlobalDataManager globalDataManager) {
        this.globalDataManager = globalDataManager;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseEntity>> getAllCourses() {
        List<CourseEntity> courses = globalDataManager.academyEntity.getEntityCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/courses/update")
    public ResponseEntity<List<CourseEntity>> getUpdatedCourses() {
        List<CourseEntity> updatedCourses = globalDataManager.academyEntity.getEntityCourses();

        ///  tu będzie problem
        return ResponseEntity.ok(updatedCourses);
    }

    @PostMapping("/courses/add")
    public ResponseEntity<String> addCourse(@RequestBody CourseEntity newCourse) {
        System.out.println("New course has arrived");
        System.out.println(newCourse);
        System.out.println(newCourse.getLecturer());
        if (newCourse == null || newCourse.getName() == null || newCourse.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid course data. Name is required.");
        }
        globalDataManager.academyEntity.getEntityCourses().add(newCourse);

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
            globalDataManager.studentsData.put(newUserData.getStudentData().getId(), newUserData.getStudentData());
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


    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();

        for (UserSessionController.UserAccount account : globalDataManager.userAccounts) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", account.getId());
            user.put("login", account.getLogin());
            user.put("password", account.getPassword());
            user.put("role", account.getRole().toString());

            // Dodajemy dane studenta, jeśli rola to STUDENT
            if (account.getRole() == UserSessionController.UserRole.STUDENT) {
                StudentData studentData = globalDataManager.studentsData.get(account.getId());
                if (studentData != null) {
                    user.put("studentData", studentData);
                }
            }

            users.add(user);
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/lecturers")
    public ResponseEntity<List<LecturerEntity>> getAllLecturers() {
        try {
            List<LecturerEntity> lecturers = globalDataManager.getAllLecturers();
            if (lecturers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(lecturers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @PostMapping("/academy/update")
    public ResponseEntity<String> updateAcademy(@RequestBody AcademyEntity updatedAcademy) {
        System.out.println(updatedAcademy.getAddress());
        AcademyEntity currentAcademy = globalDataManager.getAcademyEntity();

        if (currentAcademy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }

        if (updatedAcademy.getName() != null) {
            currentAcademy.setName(updatedAcademy.getName());
        }
        if (updatedAcademy.getPhone() != null) {
            currentAcademy.setPhone(updatedAcademy.getPhone());
        }
        if (updatedAcademy.getEmail() != null) {
            currentAcademy.setEmail(updatedAcademy.getEmail());
        }

        // Aktualizacja adresu, z wykluczeniem ID
        AddressEntity currentAddress = currentAcademy.getAddress();
        AddressEntity updatedAddress = updatedAcademy.getAddress();

        if (currentAddress != null && updatedAddress != null) {
            if (updatedAddress.getStreet() != null) {
                currentAddress.setStreet(updatedAddress.getStreet());
            }
            if (updatedAddress.getCity() != null) {
                currentAddress.setCity(updatedAddress.getCity());
            }
            if (updatedAddress.getCountry() != null) {
                currentAddress.setCountry(updatedAddress.getCountry());
            }
            if (updatedAddress.getPostalCode() != null) {
                currentAddress.setPostalCode(updatedAddress.getPostalCode());
            }
        }

        // Zapis zaktualizowanego obiektu do globalDataManager
        globalDataManager.setAcademyEntity(currentAcademy);

        return ResponseEntity.ok("Academy updated successfully.");
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
    @PostMapping("dean/change")
    public ResponseEntity<String> changeDean(@RequestBody LecturerEntity newDean) {
        AcademyEntity academyEntity = globalDataManager.getAcademyEntity();

        if (academyEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }

        if (newDean == null || newDean.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid dean data.");
        }

        try {
            System.out.println("New dean:\n");
            System.out.println(newDean);
            academyEntity.setDean(newDean);


            globalDataManager.setAcademyEntity(academyEntity);

            return ResponseEntity.ok("Dean changed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error changing dean: " + e.getMessage());
        }
    }




}
