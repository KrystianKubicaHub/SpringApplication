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
    public List<EnrollmentEntity> enrollments;


    public StudentData() {
        super();
        FAKE_DATA.setPersonsData(this);
        this.enrollments = FAKE_DATA.getAllEnrollments();
    }
}
