package bdbt_bada_project.SpringApplication.entities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonEntity {

    public Integer id;

    public String firstName;

    public String lastName;

    public String PESELNumber;

    public String email;

    public String phoneNumber;

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonEntity(Integer id, String firstName, String lastName, String PESELNumber, String email, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.PESELNumber = PESELNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public PersonEntity() {
        this.id = 1;
        this.firstName = "firstName";
        this.lastName = "lastName";
        this.PESELNumber = "PESELNumber";
        this.email = "email";
        this.phoneNumber = "phoneNumber";
    }

    public Integer getId() {
        return id;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }


}
