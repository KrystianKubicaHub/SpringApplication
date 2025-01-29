package bdbt_bada_project.SpringApplication.entities;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StudentData extends PersonEntity {

    public int indexNumber;
    public String studySince;
    public int totalECTS;
    public List<FieldOfStudyEntity> fieldOfStudy;
    private final List<EnrollmentEntity> enrollments;

    public List<EnrollmentEntity> getEnrollments() {
        return enrollments;
    }

    public StudentData(Integer id, String firstName, String lastName, String PESELNumber, String email, String phoneNumber,
                       List<EnrollmentEntity> enrollments, List<FieldOfStudyEntity> fieldOfStudy,
                       int totalECTS, String studySince, int indexNumber) {
        super(id, firstName, lastName, PESELNumber, email, phoneNumber);
        this.enrollments = enrollments;
        this.fieldOfStudy = fieldOfStudy;
        this.totalECTS = totalECTS;
        this.studySince = studySince;
        this.indexNumber = indexNumber;
    }

    @Override
    public String toString() {
        return "StudentData{" +
                "indexNumber=" + indexNumber +
                ", studySince='" + studySince + '\'' +
                ", totalECTS=" + totalECTS +
                ", fieldOfStudy=" + fieldOfStudy +
                ", enrollments=" + enrollments +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", PESELNumber='" + PESELNumber + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    public StudentData() {
        super();
        this.enrollments = new ArrayList<>();
    }

    public boolean removeEnrollmentByIndex(int i) {
        if (i >= 0 && i < enrollments.size()) {
            enrollments.remove(i);
            return true;
        } else {
            return false;
        }
    }

    public boolean addNewEnrollment(CourseEntity courseEntity) {
        if (courseEntity == null) {
            return false;
        }

        int newId = 1;
        List<EnrollmentEntity> enrollments = this.getEnrollments();

        if (enrollments != null && !enrollments.isEmpty()) {
            newId = enrollments.stream()
                    .mapToInt(EnrollmentEntity::getId)
                    .max()
                    .orElse(0) + 1;
        }

        Date currentDate = new Date();
        EnrollmentEntity newEnrollment = new EnrollmentEntity(courseEntity, currentDate, newId);

        if (enrollments != null) {
            enrollments.add(newEnrollment);
            return true;
        } else {
            return false;
        }
    }
}
