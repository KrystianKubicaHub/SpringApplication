package bdbt_bada_project.SpringApplication.entities;

import java.util.Date;

public class EnrollmentEntity {
    private CourseEntity course;
    private Date enrollmentDate;

    public EnrollmentEntity(CourseEntity course, Date enrollmentDate) {
        this.course = course;
        this.enrollmentDate = enrollmentDate;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }
}
