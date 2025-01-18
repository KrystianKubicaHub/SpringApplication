package bdbt_bada_project.SpringApplication;

import bdbt_bada_project.SpringApplication.DataModels.StudentData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ResignationController {

    @PostMapping("/resign/{courseId}")
    public ResponseEntity<String> resignFromCourse(@PathVariable Integer courseId) {
        StudentData student = new StudentData();

        /*
        if (student.removeEnrollmentById(courseId)) {
            System.out.println("Successfully removed enrollment for course ID: " + courseId);
            return ResponseEntity.ok("Resignation successful");
        } else {
            System.out.println("Failed to remove enrollment for course ID: " + courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found for course ID: " + courseId);
        }

        remove return statement
         */
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found for course ID: " + courseId);
    }

    @PostMapping("/updateFirstName")
    public ResponseEntity<String> updateFirstName(@RequestBody String firstName) {
        StudentData student = new StudentData();
        student.setFirstName(firstName);
        System.out.println("Updated first name to: " + firstName);
        return ResponseEntity.ok("First name updated successfully");
    }

    @PostMapping("/updateLastName")
    public ResponseEntity<String> updateLastName(@RequestBody String lastName) {
        StudentData student = new StudentData();
        student.setLastName(lastName);
        System.out.println("Updated last name to: " + lastName);
        return ResponseEntity.ok("Last name updated successfully");
    }

    @PostMapping("/updateEmail")
    public ResponseEntity<String> updateEmail(@RequestBody String email) {
        StudentData student = new StudentData();
        student.setEmail(email);
        System.out.println("Updated email to: " + email);
        return ResponseEntity.ok("Email updated successfully");
    }

    @PostMapping("/updatePhoneNumber")
    public ResponseEntity<String> updatePhoneNumber(@RequestBody String phoneNumber) {
        StudentData student = new StudentData();
        student.setPhoneNumber(phoneNumber);
        System.out.println("Updated phone number to: " + phoneNumber);
        return ResponseEntity.ok("Phone number updated successfully");
    }
}
