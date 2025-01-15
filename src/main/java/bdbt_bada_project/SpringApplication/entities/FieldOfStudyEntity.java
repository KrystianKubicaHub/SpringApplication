package bdbt_bada_project.SpringApplication.entities;

public class FieldOfStudyEntity {
    private String name;

    @Override
    public String toString() {
        return "FieldOfStudyEntity{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public FieldOfStudyEntity(String name) {
        this.name = name;
    }
}
