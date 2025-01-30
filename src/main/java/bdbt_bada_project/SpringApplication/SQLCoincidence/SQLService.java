package bdbt_bada_project.SpringApplication.SQLCoincidence;

import bdbt_bada_project.SpringApplication.Controllers.AdminController;
import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SQLService {

    private final JdbcTemplate jdbcTemplate;
    private final GlobalDataManager globalDataManager;

    @Autowired
    public SQLService(JdbcTemplate jdbcTemplate, GlobalDataManager globalDataManager) {
        System.out.println("It has been created");
        this.jdbcTemplate = jdbcTemplate;
        this.globalDataManager = globalDataManager;
        loadUserAccountsFromDatabase();
        loadAcademyEntity();
        loadStudentsFromDatabase();
        loadLecturersFromDatabase();
        loadCoursesFromDatabase();
        loadFieldsOfStudyFromDatabase();
        loadEnrollmentsFromDatabase();
    }

    public boolean setDean(int newDeanId) {
        String sql = "UPDATE academy_entities SET dean_id = ? WHERE id_unit = 1";

        try {
            int rowsAffected = jdbcTemplate.update(sql, newDeanId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error" + e.getMessage());
            return false;
        }
    }


    public void loadAcademyEntity() {
        final int ACADEMY_ID = 1;
        String queryAcademy = "SELECT ID_UNIT, NAME, PHONE, EMAIL, ADDRESS_ID FROM ACADEMY_ENTITIES WHERE ID_UNIT = ?";
        String queryAddress = "SELECT ID, STREET, HOUSE_NUMBER, APARTMENT_NUMBER, CITY, POSTAL_CODE, COUNTRY FROM ADDRESSES WHERE ID = ?";

        try {

            AcademyEntity academyEntity = jdbcTemplate.queryForObject(queryAcademy, new Object[]{ACADEMY_ID}, (rs, rowNum) -> {
                int idUnit = rs.getInt("ID_UNIT");
                String name = rs.getString("NAME");
                String phone = rs.getString("PHONE");
                String email = rs.getString("EMAIL");
                int addressId = rs.getInt("ADDRESS_ID");

                AddressEntity addressEntity = jdbcTemplate.queryForObject(queryAddress, new Object[]{addressId}, (addressRs, addressRowNum) -> {
                    Integer id = addressRs.getInt("ID");
                    String street = addressRs.getString("STREET");
                    String houseNumber = addressRs.getString("HOUSE_NUMBER");
                    String apartmentNumber = addressRs.getString("APARTMENT_NUMBER");
                    String city = addressRs.getString("CITY");
                    String postalCode = addressRs.getString("POSTAL_CODE");
                    String country = addressRs.getString("COUNTRY");
                    return new AddressEntity(id, street, houseNumber, apartmentNumber, city, postalCode, country);
                });


                return new AcademyEntity(idUnit, name, phone, email, addressEntity, null);
            });

            // Przypisz wynik do globalnego menadżera danych
            globalDataManager.academyEntity = academyEntity;
            globalDataManager.academyEntity.setDean(new LecturerEntity(205,
                    "DeanName",
                    "DeanLastName",
                    "04230809071",
                    "dean@wp.pl",
                    "606309379",
                    "majster",
                    "Picie wódeczki"
            ));
            System.out.println("AcademyEntity loaded successfully: " + academyEntity);
        } catch (Exception e) {
            System.err.println("Error while loading AcademyEntity: " + e.getMessage());
        }
    }

    public void loadDeanFromDatabase() {
        String sql = "SELECT dean_id FROM academy_entities WHERE id_unit = 1";

        try {
            Integer deanId = jdbcTemplate.queryForObject(sql, Integer.class);

            if (deanId != null) {
                System.out.println("Dean ID: " + deanId);


                LecturerEntity dean = globalDataManager.getAllLecturers().stream()
                        .filter(lecturer -> lecturer.getId().equals(deanId))
                        .findFirst()
                        .orElse(null);

                if (dean != null) {
                    globalDataManager.academyEntity.setDean(dean);
                }
            }
        } catch (Exception e) {
            System.err.println("Error" + e.getMessage());
            e.printStackTrace();
        }
    }




    public void loadUserAccountsFromDatabase() {
        String query = "SELECT ID, LOGIN, PASSWORD, ROLE FROM USER_ACCOUNTS";

        try {
            List<UserSessionController.UserAccount> accounts = jdbcTemplate.query(query, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                String login = rs.getString("LOGIN");
                String password = rs.getString("PASSWORD");
                String roleString = rs.getString("ROLE");
                UserSessionController.UserRole role = UserSessionController.UserRole.valueOf(roleString);
                return new UserSessionController.UserAccount(id, login, password, role);
            });
            System.out.println(accounts.get(99));
            globalDataManager.userAccounts.clear();
            globalDataManager.userAccounts.addAll(accounts);
            //globalDataManager.studentsData = FAKE_DATA.generateStudentDataEntries(globalDataManager.userAccounts);
            //globalDataManager.lecturersData = FAKE_DATA.generateLecturerDataEntries(globalDataManager.userAccounts);
        } catch (Exception e) {
            System.err.println("Error while loading user accounts from database: " + e.getMessage());
        }
    }

    private void loadStudentsFromDatabase() {
        // Zapytanie SQL do pobrania wszystkich rekordów z person_entities
        String personQuery = "SELECT id, first_name, last_name, pesel_number, email, phone_number FROM person_entities";

        // Zapytanie SQL do pobrania wszystkich rekordów z student_entities
        String studentQuery = "SELECT id, index_number, study_since, total_ects FROM student_entities";

        try {
            // Pobranie danych z person_entities
            Map<Integer, Map<String, Object>> personData = jdbcTemplate.query(personQuery, rs -> {
                Map<Integer, Map<String, Object>> result = new HashMap<>();
                while (rs.next()) {
                    Map<String, Object> person = new HashMap<>();
                    person.put("firstName", rs.getString("first_name"));
                    person.put("lastName", rs.getString("last_name"));
                    person.put("peselNumber", rs.getString("pesel_number"));
                    person.put("email", rs.getString("email"));
                    person.put("phoneNumber", rs.getString("phone_number"));
                    result.put(rs.getInt("id"), person);
                }
                return result;
            });

            // Pobranie danych z student_entities
            List<Map<String, Object>> students = jdbcTemplate.query(studentQuery, (rs, rowNum) -> {
                Map<String, Object> student = new HashMap<>();
                student.put("id", rs.getInt("id"));
                student.put("indexNumber", rs.getInt("index_number"));
                student.put("studySince", rs.getString("study_since"));
                student.put("totalECTS", rs.getInt("total_ects"));
                return student;
            });

            // Iteracja przez student_entities i sprawdzanie warunków
            for (Map<String, Object> student : students) {
                Integer studentId = (Integer) student.get("id");

                // Sprawdzenie, czy istnieje odpowiadający rekord w userAccounts
                Optional<UserSessionController.UserAccount> matchingAccount = globalDataManager.userAccounts.stream()
                        .filter(account -> account.getId() == studentId)
                        .filter(account -> account.getRole() == UserSessionController.UserRole.STUDENT)
                        .findFirst();

                if (matchingAccount.isPresent() && personData.containsKey(studentId)) {
                    // Pobranie danych z person_entities
                    Map<String, Object> person = personData.get(studentId);

                    // Utworzenie obiektu StudentData
                    StudentData studentData = new StudentData(
                            studentId,
                            (String) person.get("firstName"),
                            (String) person.get("lastName"),
                            (String) person.get("peselNumber"),
                            (String) person.get("email"),
                            (String) person.get("phoneNumber"),
                            new ArrayList<>(), // Pusta lista enrollments
                            new ArrayList<>(), // Pusta lista fieldOfStudy
                            (Integer) student.get("totalECTS"),
                            (String) student.get("studySince"),
                            (Integer) student.get("indexNumber")
                    );

                    // Dodanie obiektu do studentsData (mapa)
                    globalDataManager.studentsData.put(studentId, studentData);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania studentów z bazy danych: " + e.getMessage());
        }
    }

    private void loadLecturersFromDatabase() {
        // Upewnij się, że lista jest zainicjalizowana
        if (globalDataManager.lecturersData == null) {
            globalDataManager.lecturersData = new HashMap<>();
        }

        String personQuery = "SELECT id, first_name, last_name, pesel_number, email, phone_number FROM person_entities";
        String lecturerQuery = "SELECT id, academic_title, specialization FROM lecturer_entities";

        try {
            Map<Integer, Map<String, Object>> personData = jdbcTemplate.query(personQuery, rs -> {
                Map<Integer, Map<String, Object>> result = new HashMap<>();
                while (rs.next()) {
                    Map<String, Object> person = new HashMap<>();
                    person.put("firstName", rs.getString("first_name"));
                    person.put("lastName", rs.getString("last_name"));
                    person.put("peselNumber", rs.getString("pesel_number"));
                    person.put("email", rs.getString("email"));
                    person.put("phoneNumber", rs.getString("phone_number"));
                    result.put(rs.getInt("id"), person);
                }
                return result;
            });

            // Pobranie danych z lecturer_entities
            List<Map<String, Object>> lecturers = jdbcTemplate.query(lecturerQuery, (rs, rowNum) -> {
                Map<String, Object> lecturer = new HashMap<>();
                lecturer.put("id", rs.getInt("id"));
                lecturer.put("academicTitle", rs.getString("academic_title"));
                lecturer.put("specialization", rs.getString("specialization"));
                return lecturer;
            });

            // Iteracja przez lecturer_entities i sprawdzanie warunków
            for (Map<String, Object> lecturer : lecturers) {
                Integer lecturerId = (Integer) lecturer.get("id");

                // Sprawdzenie, czy istnieje odpowiadający rekord w userAccounts
                Optional<UserSessionController.UserAccount> matchingAccount = globalDataManager.userAccounts.stream()
                        .filter(account -> account.getId() == lecturerId)
                        .filter(account -> account.getRole() == UserSessionController.UserRole.LECTURER)
                        .findFirst();

                if (matchingAccount.isPresent() && personData.containsKey(lecturerId)) {
                    // Pobranie danych z person_entities
                    Map<String, Object> person = personData.get(lecturerId);

                    // Utworzenie obiektu LecturerEntity
                    LecturerEntity lecturerEntity = new LecturerEntity(
                            lecturerId,
                            (String) person.get("firstName"),
                            (String) person.get("lastName"),
                            (String) person.get("peselNumber"),
                            (String) person.get("email"),
                            (String) person.get("phoneNumber"),
                            (String) lecturer.get("academicTitle"),
                            (String) lecturer.get("specialization")
                    );

                    // Dodanie obiektu do lecturersData (mapa)
                    globalDataManager.lecturersData.put(lecturerId, lecturerEntity);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania wykładowców z bazy danych: " + e.getMessage());
        }
        loadDeanFromDatabase();
    }

    private void loadCoursesFromDatabase() {
        if (globalDataManager.academyEntity.getEntityCourses() == null) {
            return;
        }

        String courseQuery = "SELECT id, name, description, ects_credits, id_unit FROM courses";
        String lecturersLinkTableQuery = "SELECT lecturer_id FROM course_lecturers WHERE course_id = ?";

        try {
            // Pobranie danych z tabeli courses
            List<Map<String, Object>> courses = jdbcTemplate.query(courseQuery, (rs, rowNum) -> {
                Map<String, Object> course = new HashMap<>();
                course.put("id", rs.getInt("id"));
                course.put("name", rs.getString("name"));
                course.put("description", rs.getString("description"));
                course.put("ectsCredits", rs.getInt("ects_credits"));
                course.put("idUnit", rs.getInt("id_unit"));
                return course;
            });

            for (Map<String, Object> course : courses) {
                Integer courseId = (Integer) course.get("id");
                Integer idUnit = (Integer) course.get("idUnit");
                LecturerEntity lecturer = null;

                // Sprawdzenie, czy id_unit jest równe 1
                if (idUnit == 1) {
                    // Pobierz ID wykładowców przypisanych do kursu z tabeli łączącej
                    List<Integer> lecturerIds = jdbcTemplate.query(lecturersLinkTableQuery, new Object[]{courseId}, (rs, rowNum) -> rs.getInt("lecturer_id"));

                    if (!lecturerIds.isEmpty()) {
                        // Wybierz losowego wykładowcę z listy
                        Random random = new Random();
                        Integer randomLecturerId = lecturerIds.get(random.nextInt(lecturerIds.size()));

                        // Pobierz wykładowcę z globalDataManager.lecturersData
                        lecturer = globalDataManager.lecturersData.get(randomLecturerId);

                        if (lecturer == null) {
                            System.err.println("Warning: Lecturer with ID " + randomLecturerId + " not found in globalDataManager.lecturersData");
                        }
                    }
                }

                CourseEntity courseEntity = new CourseEntity(
                        courseId,
                        (String) course.get("name"),
                        (String) course.get("description"),
                        (Integer) course.get("ectsCredits"),
                        lecturer
                );

                globalDataManager.academyEntity.addCourse(courseEntity);
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania kursów z bazy danych: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFieldsOfStudyFromDatabase() {
        String query = "SELECT id_field, field_name, study_level, duration_in_semesters, description, academy_id FROM field_of_study_entities";

        try {
            List<FieldOfStudyEntity> fieldsOfStudy = jdbcTemplate.query(query, (rs, rowNum) -> {
                int idField = rs.getInt("id_field");
                String fieldName = rs.getString("field_name");
                String studyLevelStr = rs.getString("study_level");
                short durationInSemesters = rs.getShort("duration_in_semesters");
                String description = rs.getString("description");
                int academyId = rs.getInt("academy_id");

                FieldOfStudyEntity.StudyLevel studyLevel = FieldOfStudyEntity.StudyLevel.valueOf(studyLevelStr);

                FieldOfStudyEntity fieldOfStudyEntity = new FieldOfStudyEntity(idField, fieldName, studyLevel, durationInSemesters, description);


                globalDataManager.academyEntity.addFieldOfStudy(fieldOfStudyEntity);

                return fieldOfStudyEntity;
            });

        } catch (Exception e) {
            System.err.println("Error numberr -1-1-1-: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void updateAcademyInDatabase(AcademyEntity academyEntity) {
        String updateAcademyQuery = "UPDATE academy_entities SET name = ?, phone = ?, email = ? WHERE id_unit = ?";
        jdbcTemplate.update(updateAcademyQuery,
                academyEntity.getName(),
                academyEntity.getPhone(),
                academyEntity.getEmail(),
                academyEntity.getIdUnit());
    }

    public void updateAddressInDatabase(AddressEntity addressEntity) {
        String updateAddressQuery = "UPDATE Addresses SET street = ?, city = ?, country = ?, postal_code = ? WHERE id = ?";
        jdbcTemplate.update(updateAddressQuery,
                addressEntity.getStreet(),
                addressEntity.getCity(),
                addressEntity.getCountry(),
                addressEntity.getPostalCode(),
                addressEntity.getId());
    }








    public void addUserAccount(UserSessionController.UserAccount userAccount) {
        String query = "INSERT INTO USER_ACCOUNTS (ID, LOGIN, PASSWORD, ROLE, ACADEMY_ID) VALUES (?, ?, ?, ?, ?)"; // 🟢 Poprawione: dodano brakujące `?`

        try {
            jdbcTemplate.update(query,
                    userAccount.getId(),
                    userAccount.getLogin(),
                    userAccount.getPassword(),
                    userAccount.getRole().name(),
                    1 // 🟢 Domyślne ACADEMY_ID (jeśli zawsze 1, można zostawić tak)
            );
            System.out.println("user account added: " + userAccount.getLogin());
        } catch (Exception e) {
            System.err.println("Error" + e.getMessage());
        }
    }


    public void addStudent(AdminController.NewUserRequest newUserRequest, int studentId) {
        if (newUserRequest == null || newUserRequest.getStudentData() == null) {
            System.err.println("Error: Invalid student data!");
            return;
        }

        StudentData studentData = newUserRequest.getStudentData();

        String personSql = "INSERT INTO person_entities (id, first_name, last_name, pesel_number, phone_number, email) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(personSql,
                    studentId,
                    studentData.getFirstName(),
                    studentData.getLastName(),
                    studentData.getPESELNumber(),
                    studentData.getPhoneNumber(),
                    studentData.getEmail()
            );


            String studentSql = "INSERT INTO student_entities (id, index_number, study_since, total_ects) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(studentSql,
                    studentId,
                    studentData.indexNumber,
                    studentData.studySince,
                    studentData.totalECTS
            );

            System.out.println("Student added successfully: " + studentData.getFirstName() + " " + studentData.getLastName());
        } catch (Exception e) {
            System.err.println("Error inserting student into database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean deleteFieldOfStudyById(int id) {
        try {
            String sql = "DELETE FROM field_of_study_entities WHERE id_field = ?";
            int rowsAffected = jdbcTemplate.update(sql, id);

            if (rowsAffected > 0) {
                System.out.println("Field of study with ID " + id + " deleted successfully.");
                return true;
            } else {
                System.err.println("No field of study found with ID " + id + ". Nothing was deleted.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error while deleting field of study with ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    public boolean addFieldOfStudy(FieldOfStudyEntity newField) {
        try {
            String sql = "INSERT INTO field_of_study_entities (field_name, study_level, duration_in_semesters, description, academy_id) VALUES (?, ?, ?, ?, ?)";

            int rowsAffected = jdbcTemplate.update(sql,
                    newField.getFieldName(),
                    newField.getStudyLevel().toString(),
                    newField.getDurationInSemesters(),
                    newField.getDescription(),
                    1
            );

            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error adding field of study: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourseById(int id) {
        try {
            String sql = "DELETE FROM courses WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, id);

            if (rowsAffected > 0) {
                System.out.println("Course with ID " + id + " deleted successfully.");
                return true;
            } else {
                System.err.println("No course found with ID " + id + ". Nothing was deleted.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error while deleting course with ID " + id + ": " + e.getMessage());
            return false;
        }
    }


    public boolean addCourse(CourseEntity course) {
        String getMaxIdQuery = "SELECT COALESCE(MAX(id), 0) + 1 FROM courses";
        int nextId = jdbcTemplate.queryForObject(getMaxIdQuery, Integer.class);

        String sql = "INSERT INTO courses (id, name, description, ects_credits, id_unit) VALUES (?, ?, ?, ?, ?)";

        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    nextId, // Nowe ID = max(ID) + 1
                    course.getName(),
                    course.getDescription(),
                    course.getEctsCredits(),
                    1
            );

            if (rowsAffected > 0) {
                course.setId(nextId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error inserting course into database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int addEnrollment(int userId, String courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, SYSDATE)";
        int enrollmentId = -1;

        try {
            String getMaxIdSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM enrollments";
            int newId = jdbcTemplate.queryForObject(getMaxIdSql, Integer.class);

            int rowsAffected = jdbcTemplate.update(sql, userId, Integer.parseInt(courseId));

            if (rowsAffected > 0) {
                enrollmentId = newId;
                System.out.println("Enrollment added successfully! ID: " + enrollmentId);
            } else {
                System.err.println("Failed to insert enrollment.");
            }

        } catch (Exception e) {
            System.err.println("Error inserting enrollment: " + e.getMessage());
            e.printStackTrace();
        }

        return enrollmentId;
    }


    public void loadEnrollmentsFromDatabase() {
        String sql = "SELECT id, student_id, course_id, enrollment_date FROM enrollments";

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : results) {
                int enrollmentId = ((Number) row.get("id")).intValue(); // Konwersja BigDecimal -> int
                int studentId = ((Number) row.get("student_id")).intValue(); // Pobranie ID studenta
                int courseId = ((Number) row.get("course_id")).intValue(); // Pobranie ID kursu
                Date enrollmentDate = (Date) row.get("enrollment_date");

                // Pobranie CourseEntity na podstawie ID kursu
                CourseEntity course = globalDataManager.academyEntity.getEntityCourses()
                        .stream()
                        .filter(c -> c.getId().equals(courseId))
                        .findFirst()
                        .orElse(null);

                if (course == null) {
                    System.err.println("Course with ID " + courseId + " not found in globalDataManager.");
                    continue;
                }
                EnrollmentEntity enrollment = new EnrollmentEntity(course, enrollmentDate, enrollmentId);


                StudentData student = globalDataManager.studentsData.get(studentId);
                if (student == null) {
                    System.err.println("Student with ID " + studentId + " not found in globalDataManager.");
                    continue;
                }

                System.out.println("Enrollment: " + enrollment.getId() + " For student " + student.firstName);

                student.getEnrollments().add(enrollment);
                System.out.println(student.getEnrollments().size());
            }

            System.out.println("All enrollments have been successfully assigned to students!");

        } catch (Exception e) {
            System.err.println("Error loading enrollments from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean updateFirstName(int studentId, String firstName) {
        String sql = "UPDATE person_entities SET first_name = ? WHERE id = ?";
        return executeUpdate(sql, firstName, studentId);
    }

    public boolean updateLastName(int studentId, String lastName) {
        String sql = "UPDATE person_entities SET last_name = ? WHERE id = ?";
        return executeUpdate(sql, lastName, studentId);
    }

    public boolean updateEmail(int studentId, String email) {
        String sql = "UPDATE person_entities SET email = ? WHERE id = ?";
        return executeUpdate(sql, email, studentId);
    }

    public boolean updatePhoneNumber(int studentId, String phoneNumber) {
        String sql = "UPDATE person_entities SET phone_number = ? WHERE id = ?";
        return executeUpdate(sql, phoneNumber, studentId);
    }
    private boolean executeUpdate(String sql, String newValue, int studentId) {
        try {
            int rowsAffected = jdbcTemplate.update(sql, newValue, studentId);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating student data: " + e.getMessage());
            return false;
        }
    }




}
