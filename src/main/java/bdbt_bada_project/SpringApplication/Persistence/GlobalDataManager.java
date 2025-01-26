package bdbt_bada_project.SpringApplication.Persistence;

import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.AcademyEntity;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.LecturerEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class GlobalDataManager {

    public final List<UserSessionController.UserAccount> userAccounts = new ArrayList<>();
    public final Map<Integer, UserSessionController.UserSession> activeSessions = new HashMap<>();
    public final Map<String, UserSessionController.UserSession> sessionTokens = new HashMap<>();

    public final Map<Integer, StudentData> studentsData;
    public final Map<Integer, LecturerEntity> lecturersData;

    public AcademyEntity academyEntity;

    public GlobalDataManager() {
        this.userAccounts.addAll(FAKE_DATA.getAccountsCredentialsFromSQL(FAKE_DATA.numberOfStudents));

        studentsData = FAKE_DATA.generateStudentDataEntries(this.userAccounts);
        lecturersData = FAKE_DATA.generateLecturerDataEntries(this.userAccounts);

        academyEntity = FAKE_DATA.loadFromSQLAcademyEntity();
    }

    public String addSession(UserSessionController.UserSession session) {
        activeSessions.put(session.getId(), session);
        String token = UUID.randomUUID().toString();
        sessionTokens.put(token, session);
        return token;
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

    public Collection<UserSessionController.UserSession> getActiveSessions() {return activeSessions.values();}

    public boolean isSessionActive(int userId) {return activeSessions.containsKey(userId);}

    public List<LecturerEntity> getAllLecturers() {
        return new ArrayList<>(lecturersData.values());
    }

    public AcademyEntity getAcademyEntity() {
        return academyEntity;
    }

    public void setAcademyEntity(AcademyEntity newAcademyEntity) {
        academyEntity = newAcademyEntity;
    }
}
