package bdbt_bada_project.SpringApplication.SQLCoincidence;

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
    }
    public void loadAcademyEntity() {
        final int ACADEMY_ID = 1;
        String queryAcademy = "SELECT ID_UNIT, NAME, PHONE, EMAIL, ADDRESS_ID FROM ACADEMY_ENTITIES WHERE ID_UNIT = ?";
        String queryAddress = "SELECT ID, STREET, HOUSE_NUMBER, APARTMENT_NUMBER, CITY, POSTAL_CODE, COUNTRY FROM ADDRESSES WHERE ID = ?";

        try {
            // Pobierz dane Akademii
            AcademyEntity academyEntity = jdbcTemplate.queryForObject(queryAcademy, new Object[]{ACADEMY_ID}, (rs, rowNum) -> {
                int idUnit = rs.getInt("ID_UNIT");
                String name = rs.getString("NAME");
                String phone = rs.getString("PHONE");
                String email = rs.getString("EMAIL");
                int addressId = rs.getInt("ADDRESS_ID");

                // Pobierz dane adresu powiązanego z akademią
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

                // Zwróć obiekt AcademyEntity z przypisanym adresem i dean ustawionym na null
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
        String query = "INSERT INTO USER_ACCOUNTS (ID, LOGIN, PASSWORD, ROLE) VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.update(query, userAccount.getId(), userAccount.getLogin(), userAccount.getPassword(), userAccount.getRole().name());
            System.out.println("User account added: " + userAccount.getLogin());
        } catch (Exception e) {
            System.err.println("Error while adding user account: " + e.getMessage());
        }
    }
}
