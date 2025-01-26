package bdbt_bada_project.SpringApplication.entities;

public class FieldOfStudyEntity {
    private int idField; // ID of the field
    private String fieldName; // Name of the field
    private StudyLevel studyLevel; // Level of study
    private short durationInSemesters; // Duration in semesters
    private String description; // Description of the field

    public enum StudyLevel {
        BACHELOR,
        MASTER,
        ENGINEER,
        DOCTORATE
    }

    public FieldOfStudyEntity() {
    }

    public FieldOfStudyEntity(int idField, String fieldName, StudyLevel studyLevel, short durationInSemesters, String description) {
        this.idField = idField;
        this.fieldName = fieldName;
        this.studyLevel = studyLevel;
        this.durationInSemesters = durationInSemesters;
        this.description = description;
    }

    public int getIdField() {
        return idField;
    }

    public void setIdField(int idField) {
        this.idField = idField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public short getDurationInSemesters() {
        return durationInSemesters;
    }

    public void setDurationInSemesters(short durationInSemesters) {
        this.durationInSemesters = durationInSemesters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FieldOfStudyEntity{" +
                "idField=" + idField +
                ", fieldName='" + fieldName + '\'' +
                ", studyLevel=" + studyLevel +
                ", durationInSemesters=" + durationInSemesters +
                ", description='" + description + '\'' +
                '}';
    }
}
