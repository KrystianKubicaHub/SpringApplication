package bdbt_bada_project.SpringApplication.SQLCoincidence;

import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
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
            globalDataManager.studentsData = FAKE_DATA.generateStudentDataEntries(globalDataManager.userAccounts);
            globalDataManager.lecturersData = FAKE_DATA.generateLecturerDataEntries(globalDataManager.userAccounts);
            globalDataManager.academyEntity = FAKE_DATA.loadFromSQLAcademyEntity();

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
