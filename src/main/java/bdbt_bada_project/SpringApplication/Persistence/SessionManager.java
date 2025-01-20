package bdbt_bada_project.SpringApplication.Persistence;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SessionManager {

    private final Map<Integer, UserSessionController.UserSession> activeSessions = new HashMap<>();
    private final Map<String, UserSessionController.UserSession> sessionTokens = new HashMap<>();

    public String addSession(UserSessionController.UserSession session) {
        activeSessions.put(session.getId(), session);
        String token = UUID.randomUUID().toString(); // Generowanie tokena sesji
        sessionTokens.put(token, session);
        return token;
    }

    public boolean removeSession(int userId) {
        UserSessionController.UserSession session = activeSessions.remove(userId);
        if (session != null) {
            sessionTokens.values().remove(session);
            return true;
        }
        return false;
    }

    public boolean removeSessionByToken(String token) {
        UserSessionController.UserSession session = sessionTokens.remove(token);
        if (session != null) {
            activeSessions.remove(session.getId());
            return true;
        }
        return false;
    }

    public UserSessionController.UserSession getSessionByToken(String token) {
        return sessionTokens.get(token);
    }

    public Collection<UserSessionController.UserSession> getActiveSessions() {
        return activeSessions.values();
    }

    public boolean isSessionActive(int userId) {
        return activeSessions.containsKey(userId);
    }
}
