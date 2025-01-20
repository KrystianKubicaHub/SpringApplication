package bdbt_bada_project.SpringApplication.entities;

import java.io.Serializable;

public class AcademyEntity implements Serializable {
    private int idUnit;
    private String name;
    private String phone;
    private String email;
    private int addressId;
    private int dean;

    public AcademyEntity() {
    }

    public AcademyEntity(int idUnit, String name, String phone, String email, int addressId, int dean) {
        this.idUnit = idUnit;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.addressId = addressId;
        this.dean = dean;
    }

    public int getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(int idUnit) {
        this.idUnit = idUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getDean() {
        return dean;
    }

    public void setDean(int dean) {
        this.dean = dean;
    }

    @Override
    public String toString() {
        return "AcademyEntity{" +
                "idUnit=" + idUnit +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", addressId=" + addressId +
                ", dean=" + dean +
                '}';
    }
}
