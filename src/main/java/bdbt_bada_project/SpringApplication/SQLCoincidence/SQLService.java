package bdbt_bada_project.SpringApplication.SQLCoincidence;

import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.entities.AcademyEntity;
import bdbt_bada_project.SpringApplication.entities.AddressEntity;
import bdbt_bada_project.SpringApplication.entities.LecturerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
    }
    public void loadAcademyEntity() {
        final int ACADEMY_ID = 1; // Stała ID encji AcademyEntity
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
