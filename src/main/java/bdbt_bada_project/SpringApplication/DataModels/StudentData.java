package bdbt_bada_project.SpringApplication.DataModels;

import bdbt_bada_project.SpringApplication.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import bdbt_bada_project.SpringApplication.entities.FieldOfStudyEntity;
import bdbt_bada_project.SpringApplication.entities.PersonEntity;
import java.util.List;


public class StudentData extends PersonEntity {

    public int indexNumber;
    public String studySince;
    public int totalECTS;
    public List<FieldOfStudyEntity> fieldOfStudy;
    private List<EnrollmentEntity> enrollments;

    public List<EnrollmentEntity> getEnrollments() {
        return enrollments;
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
        FAKE_DATA.setPersonsData(this);
        this.enrollments = FAKE_DATA.getAllEnrollments();
    }

    public boolean removeEnrollmentByIndex(int i) {
        if (i >= 0 && i < enrollments.size()) {
            enrollments.remove(i);
            return true;
        } else {
            return false;
        }
    }

}
