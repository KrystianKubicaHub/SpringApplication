package bdbt_bada_project.SpringApplication.Controllers;

import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.SQLCoincidence.SQLService;
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
    private final SQLService sqlService;

    public AdminController(GlobalDataManager globalDataManager, SQLService sqlService) {
        this.globalDataManager = globalDataManager;
        this.sqlService = sqlService;
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
    public ResponseEntity<String> addCourse(@RequestBody Map<String, Object> newCourseData) {
        System.out.println("New course has arrived: " + newCourseData);

        if (!newCourseData.containsKey("name") || !newCourseData.containsKey("ectsCredits") || !newCourseData.containsKey("lecturerId")) {
            return ResponseEntity.badRequest().body("Invalid course data. Name, ECTS credits, and Lecturer ID are required.");
        }

        try {
            String name = (String) newCourseData.get("name");
            String description = (String) newCourseData.getOrDefault("description", "");
            int ectsCredits = (int) newCourseData.get("ectsCredits");

            // 🔧 Poprawne parsowanie `lecturerId`
            int lecturerId;
            if (newCourseData.get("lecturerId") instanceof Integer) {
                lecturerId = (int) newCourseData.get("lecturerId");
            } else {
                lecturerId = Integer.parseInt((String) newCourseData.get("lecturerId"));
            }

            // Wyszukiwanie wykładowcy po ID
            LecturerEntity lecturer = globalDataManager.getAllLecturers().stream()
                    .filter(l -> l.getId() == lecturerId)
                    .findFirst()
                    .orElse(null);

            if (lecturer == null) {
                return ResponseEntity.badRequest().body("Invalid Lecturer ID: " + lecturerId);
            }

            // Tworzenie nowego kursu
            CourseEntity newCourse = new CourseEntity(null, name, description, ectsCredits, lecturer);

            // Próba zapisania kursu w bazie danych
            boolean isAddedToDB = sqlService.addCourse(newCourse);

            if (!isAddedToDB) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save course in the database.");
            }

            // Dodanie kursu do pamięci po udanym zapisie do bazy danych
            globalDataManager.academyEntity.getEntityCourses().add(newCourse);

            return ResponseEntity.ok("Course added successfully.");
        } catch (Exception e) {
            System.err.println("❌ ERROR in addCourse(): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
        }
    }


    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable int courseId) {
        System.out.println("Received request to delete course ID: " + courseId);

        boolean courseExists = globalDataManager.academyEntity.getEntityCourses().stream()
                .anyMatch(course -> course.getId() == courseId);

        if (!courseExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }

        boolean deletedFromDB = sqlService.deleteCourseById(courseId);




        if (!deletedFromDB) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete course from database.");
        }
        globalDataManager.academyEntity.getEntityCourses().removeIf(course -> course.getId() == courseId);

        System.out.println("Course ID " + courseId + " deleted successfully.");
        return ResponseEntity.ok("Course deleted successfully.");
    }






    @PostMapping("/users/add")
    public ResponseEntity<String> addUser(@RequestBody NewUserRequest newUserData) {
        int newId = getMaxUserId() + 1;
        if (newUserData == null || newUserData.getLogin() == null || newUserData.getPassword() == null || newUserData.getRole() == null) {
            return ResponseEntity.badRequest().body("Invalid user data. All fields are required.");
        }

        UserSessionController.UserAccount newUserAccount = new UserSessionController.UserAccount(newId,
                newUserData.getLogin(), newUserData.getPassword(), newUserData.getRole());
        sqlService.addUserAccount(newUserAccount);
        if (newUserData.getRole() == UserSessionController.UserRole.STUDENT) {
            if (newUserData.getStudentData() == null) {
                return ResponseEntity.badRequest().body("Student data is required for role STUDENT.");
            }
            sqlService.addStudent(newUserData, newId);
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

    public int getMaxUserId() {
        return globalDataManager.userAccounts.stream()
                .mapToInt(UserSessionController.UserAccount::getId)
                .max()
                .orElse(0); // Jeśli lista jest pusta, zwróci 0
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


    @GetMapping("/fieldsOfStudy")
    public ResponseEntity<List<FieldOfStudyEntity>> getAllFieldOfStudy() {
        List<FieldOfStudyEntity> fieldOfStudyEntities = globalDataManager.academyEntity.getFieldsOfStudy();
        return ResponseEntity.ok(fieldOfStudyEntities);
    }

    @DeleteMapping("/fieldsOfStudy/{id}")
    public ResponseEntity<String> deleteFieldOfStudy(@PathVariable int id) {
        boolean isDeleted = sqlService.deleteFieldOfStudyById(id);

        System.out.println("Proścba o usunięcie " + id);
        if (isDeleted) {
            globalDataManager.academyEntity.getFieldsOfStudy()
                    .removeIf(field -> field.getIdField() == id);
            return ResponseEntity.ok("Field of study deleted successfully.");
        } else {
            return ResponseEntity.status(500).body("Failed to delete field of study.");
        }
    }
    @PostMapping("/fieldsOfStudy/add")
    public ResponseEntity<String> addFieldOfStudy(@RequestBody FieldOfStudyEntity newField) {
        if (newField == null || newField.getFieldName() == null || newField.getFieldName().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid field of study data. Name is required.");
        }

        boolean isAdded = sqlService.addFieldOfStudy(newField);

        if (isAdded) {
            globalDataManager.academyEntity.addFieldOfStudy(newField);
            return ResponseEntity.ok("Field of study added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add field of study.");
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
            System.out.println(academyEntity);
            return ResponseEntity.ok(academyEntity);
        } else {
            System.out.println("CVhuj");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }
    }

    @PostMapping("/academy/update")
    public ResponseEntity<String> updateAcademy(@RequestBody AcademyEntity updatedAcademy) {
        System.out.println("Received update request for academy: " + updatedAcademy);

        AcademyEntity currentAcademy = globalDataManager.getAcademyEntity();
        if (currentAcademy == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Academy data not found.");
        }

        // Aktualizacja pól w GlobalDataManager
        if (updatedAcademy.getName() != null) {
            currentAcademy.setName(updatedAcademy.getName());
        }
        if (updatedAcademy.getPhone() != null) {
            currentAcademy.setPhone(updatedAcademy.getPhone());
        }
        if (updatedAcademy.getEmail() != null) {
            currentAcademy.setEmail(updatedAcademy.getEmail());
        }

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

        try {
            sqlService.updateAcademyInDatabase(currentAcademy);
            assert currentAddress != null;
            sqlService.updateAddressInDatabase(currentAddress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update academy in the database: " + e.getMessage());
        }

        // Aktualizacja danych w GlobalDataManager
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
            sqlService.setDean(newDean.id);
            academyEntity.setDean(newDean);


            globalDataManager.setAcademyEntity(academyEntity);

            return ResponseEntity.ok("Dean changed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error changing dean: " + e.getMessage());
        }
    }




}
