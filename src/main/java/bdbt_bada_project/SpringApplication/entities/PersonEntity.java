package bdbt_bada_project.SpringApplication.entities;


public class PersonEntity {

    public Integer id;

    public String firstName;

    public String lastName;

    public String PESELNumber;

    public String email;

    public String phoneNumber;

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



    public void updateFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void updateLastName(String lastName) {
        this.lastName = lastName;
    }
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void updateEmail(String email) {
        this.email = email;
    }
}
