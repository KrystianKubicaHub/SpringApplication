package bdbt_bada_project.SpringApplication.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AcademyEntity implements Serializable {
    private int idUnit;
    private String name;
    private String phone;
    private String email;
    private AddressEntity address;
    private LecturerEntity dean;

    public List<CourseEntity> getEntityCourses() {
        return entityCourses;
    }

    public List<FieldOfStudyEntity> getFieldsOfStudy() {
        return fieldsOfStudy;
    }

    private final List<CourseEntity> entityCourses = new ArrayList<>();
    private final List<FieldOfStudyEntity> fieldsOfStudy = new ArrayList<>();

    public AcademyEntity() {
    }

    public AcademyEntity(int idUnit, String name, String phone, String email, AddressEntity address, LecturerEntity dean) {
        this.idUnit = idUnit;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dean = dean;
    }
    public void addCourse(CourseEntity course) {
        entityCourses.add(course);
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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public LecturerEntity getDean() {
        return dean;
    }

    public void setDean(LecturerEntity dean) {
        this.dean = dean;
    }

    @Override
    public String toString() {
        return "AcademyEntity {\n" +
                "  idUnit = " + idUnit + ",\n" +
                "  name = '" + name + "',\n" +
                "  phone = '" + phone + "',\n" +
                "  email = '" + email + "',\n" +
                "  address = {\n" +
                address + "\n" +
                "  },\n" +
                "  dean = {\n" +
                dean + "\n" +
                "  }\n" +
                "}";
    }

    public void addFieldOfStudy(FieldOfStudyEntity fieldOfStudyEntity) {
        fieldsOfStudy.add(fieldOfStudyEntity);
    }
}
