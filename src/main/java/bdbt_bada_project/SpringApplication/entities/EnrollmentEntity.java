package bdbt_bada_project.SpringApplication.entities;

import java.util.Date;

public class EnrollmentEntity {
    private final int id;
    private final CourseEntity course;
    private final Date enrollmentDate;


    public EnrollmentEntity(CourseEntity course, Date enrollmentDate, int id) {
        this.id = id;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String toString() {
        return "EnrollmentEntity{" +
                "id=" + id +
                ", course=" + course +
                ", enrollmentDate=" + enrollmentDate +
                '}';
    }

    public CourseEntity getCourse() {
        return course;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public int getId() {return id;}

}
