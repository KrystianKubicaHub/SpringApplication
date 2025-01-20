package bdbt_bada_project.SpringApplication.Persistence;

import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sessions")
public class UserSessionController {


    private final GlobalDataManager globalDataManager;

    public UserSessionController(GlobalDataManager globalDataManager) {
        this.globalDataManager = globalDataManager;

        this.globalDataManager.userAccounts.addAll(FAKE_DATA.getAccountsCredentialsFromSQL(FAKE_DATA.numberOfStudents));
        List<StudentData> allUserInfo = FAKE_DATA.getAllUserInfo(this.globalDataManager.userAccounts);
        if (allUserInfo != null) {
            for (StudentData studentData : allUserInfo) {
                if (studentData != null && studentData.getId() != null) {
                    this.globalDataManager.userStudentData.put(studentData.getId(), studentData);
                }
            }
        }
    }

    public enum UserRole {
        STUDENT,
        LECTURER,
        ADMIN
    }

    public static class UserSession {
        private int id;
        private UserRole role;

        public UserSession() {}

        public UserSession(int id, UserRole role) {
            this.id = id;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public UserRole getRole() {
            return role;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "UserSession{" +
                    "id=" + id +
                    ", role=" + role +
                    '}';
        }
    }

    public static class UserAccount {
        private int id;
        private String login;
        private String password;
        private UserRole role;

        public UserAccount(int id, String login, String password, UserRole role) {
            this.id = id;
            this.login = login;
            this.password = password;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public UserRole getRole() {
            return role;
        }
    }

    public static class LoginRequest {
        private String login;
        private String password;

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
    }

    @GetMapping("/check")
    public Object checkSession(@RequestParam String sessionToken) {
        System.out.println("Zapytanie:");
        UserSession session = globalDataManager.getSessionByToken(sessionToken);

        if (session != null) {
            // Jeśli sesja aktywna, zwracamy szczegóły użytkownika
            return new ResponseEntity<>(Map.of(
                    "active", true,
                    "id", session.getId(),
                    "role", session.getRole()
            ), HttpStatus.OK);
        } else {
            // Jeśli sesja nieaktywna, zwracamy odpowiednią informację
            return new ResponseEntity<>(Map.of(
                    "active", false,
                    "message", "Session not active."
            ), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest loginRequest) {
        UserAccount account = this.globalDataManager.userAccounts.stream()
                .filter(user -> user.getLogin().equals(loginRequest.getLogin()) && user.getPassword().equals(loginRequest.getPassword()))
                .findFirst()
                .orElse(null);

        if (account == null) {
            return "Invalid login or password.";
        }

        if (!globalDataManager.isSessionActive(account.getId())) {
            UserSession session = new UserSession(account.getId(), account.getRole());
            String token = globalDataManager.addSession(session);
            System.out.println("Generated token for user "  + token);

            return Map.of(
                    "message", "Login successful",
                    "sessionToken", token,
                    "role", account.getRole(),
                    "id", account.getId()
            );
        } else {
            return "User is already logged in.";
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String sessionToken) {
        boolean removed = globalDataManager.removeSessionByToken(sessionToken);
        return removed ? "User logged out successfully." : "No active session found for the given token.";
    }

    @PostConstruct
    public void logActiveSessionsPeriodically() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("========================================");
            System.out.println("Number of active sessions: " + globalDataManager.getActiveSessions().size());
            if (!globalDataManager.getActiveSessions().isEmpty()) {
                System.out.println("Active sessions:");
                globalDataManager.getActiveSessions().forEach(session ->
                        System.out.println("ID: " + session.getId() + ", Role: " + session.getRole())
                );
            } else {
                System.out.println("No active sessions.");
            }
            System.out.println("========================================");
        }, 0, 3, TimeUnit.SECONDS);
    }
}
