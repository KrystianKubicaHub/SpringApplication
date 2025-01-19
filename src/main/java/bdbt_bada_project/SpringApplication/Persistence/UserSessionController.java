package bdbt_bada_project.SpringApplication.Persistence;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sessions")
public class UserSessionController {

    private final List<UserSession> activeSessions = new CopyOnWriteArrayList<>();
    private final List<UserAccount> userAccounts = new ArrayList<>();

    public enum UserRole {
        STUDENT,
        LECTURER,
        ADMIN
    }

    public static class UserSession {
        private int id;
        private UserRole role;

        public UserSession() {
        }

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

    public UserSessionController() {
        userAccounts.add(new UserAccount(1, "student", "pass", UserRole.STUDENT));
        userAccounts.add(new UserAccount(2, "lecturer", "pass", UserRole.LECTURER));
        userAccounts.add(new UserAccount(3, "admin", "admin", UserRole.ADMIN));
        userAccounts.add(new UserAccount(4, "student2", "pass", UserRole.STUDENT));
        userAccounts.add(new UserAccount(5, "lecturer2", "pass", UserRole.LECTURER));
        userAccounts.add(new UserAccount(6, "admin2", "admin", UserRole.ADMIN));
    }

    @PostConstruct
    public void printActiveSessionsPeriodically() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println("Number of active sessions: " + activeSessions.size());
            activeSessions.forEach(session ->
                    System.out.println("ID: " + session.getId() + ", Role: " + session.getRole())
            );
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }, 0, 3, TimeUnit.SECONDS);
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest loginRequest) {
        UserAccount account = userAccounts.stream()
                .filter(user -> user.getLogin().equals(loginRequest.getLogin()) && user.getPassword().equals(loginRequest.getPassword()))
                .findFirst()
                .orElse(null);

        if (account == null) {
            return "Invalid login or password.";
        }

        boolean alreadyLoggedIn = activeSessions.stream().anyMatch(session -> session.getId() == account.getId());
        if (!alreadyLoggedIn) {
            UserSession session = new UserSession(account.getId(), account.getRole());
            activeSessions.add(session);
            return session;
        } else {
            return "User is already logged in.";
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody int userId) {
        activeSessions.removeIf(session -> session.getId() == userId);
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
}
