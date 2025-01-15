package bdbt_bada_project.SpringApplication.entities;

public class CourseEntity {
    private Integer id;
    private String name;
    private String description;
    private Integer ectsCredits;
    private LecturerEntity lecturer;

    public LecturerEntity getLecturer() {
        return lecturer;
    }

    public Integer getId() {
        return id;
    }

    public CourseEntity(Integer id, String name, String description, Integer ectsCredits, LecturerEntity lecturer) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ectsCredits = ectsCredits;
        this.lecturer = lecturer;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEctsCredits() {
        return ectsCredits;
    }

    public void setEctsCredits(Integer ectsCredits) {
        this.ectsCredits = ectsCredits;
    }
}
