package bdbt_bada_project.SpringApplication.entities;

public abstract class PersonEntity {
    public Integer id;
    public String firstName;
    public String lastName;
    public String PESELNumber;
    public String email;
    public String phoneNumber;

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
