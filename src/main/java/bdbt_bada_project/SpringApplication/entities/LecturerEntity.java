package bdbt_bada_project.SpringApplication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class LecturerEntity extends PersonEntity {
    private String academicTitle;
    private String specialization;

    public LecturerEntity(Integer id, String firstName, String lastName, String PESELNumber,
                          String email, String phoneNumber, String academicTitle, String specialization) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESELNumber = PESELNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.academicTitle = academicTitle;
        this.specialization = specialization;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public String getSpecialization() {
        return specialization;
    }

    @Override
    public String toString() {
        return "LecturerEntity{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", PESELNumber='" + PESELNumber + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", academicTitle='" + academicTitle + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
