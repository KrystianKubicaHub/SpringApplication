package bdbt_bada_project.SpringApplication.DataModels;

import bdbt_bada_project.SpringApplication.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import bdbt_bada_project.SpringApplication.entities.FieldOfStudyEntity;
import bdbt_bada_project.SpringApplication.entities.PersonEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StudentData extends PersonEntity {

    private static volatile StudentData instance;
    private List<EnrollmentEntity> enrollments;
    public int indexNumber;
    public String studySince;
    public int totalECTS;
    public List<FieldOfStudyEntity> fieldOfStudy;

    private StudentData() {
        FAKE_DATA.setPersonsData(this);
        this.enrollments = FAKE_DATA.getAllEnrollments();
    }

    public Boolean removeEnrollmentById(Integer courseId) {
        if (enrollments == null || enrollments.isEmpty()) {
            return false;
        }
        Optional<EnrollmentEntity> enrollmentToRemove = enrollments.stream()
                .filter(enrollment -> enrollment.getCourse().getId().equals(courseId))
                .findFirst();

        if (enrollmentToRemove.isPresent()) {
            enrollments.remove(enrollmentToRemove.get());
            return true;
        }

        return false;
    }



    public static StudentData getInstance() {
        if (instance == null) {
            synchronized (StudentData.class) {
                if (instance == null) {
                    instance = new StudentData();
                }
            }
        }
        return instance;
    }

    public List<EnrollmentEntity> getEnrollments() {
        return enrollments;
    }

    @GetMapping("/student")
    public StudentData getStudentData() {
        return StudentData.getInstance();
    }

    @Override
    public String toString() {
        return "StudentData{" +
                "enrollments=" + enrollments +
                ", indexNumber=" + indexNumber +
                ", studySince='" + studySince + '\'' +
                ", totalECTS=" + totalECTS +
                ", fieldOfStudy=" + fieldOfStudy +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", PESELNumber='" + PESELNumber + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
