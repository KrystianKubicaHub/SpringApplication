package bdbt_bada_project.SpringApplication.Persistence;

import bdbt_bada_project.SpringApplication.Helpers.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.StudentData;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/api")
public class StateController {

    private static final StudentData instance = FAKE_DATA.createExampleStudent();
    private final ScheduledExecutorService scheduler;
    private List<CourseEntity> serverCourses;

    public StateController() {
        serverCourses = FAKE_DATA.generateCourses(20);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println(serverCourses);

        //FAKE_DATA.startUpdatingTask(scheduler, instance);
        //FAKE_DATA.startRemovingEnrollmentTask(scheduler, instance);
        ///FAKE_DATA.stopUpdatingTask(scheduler);
    }


    /*
    @PostMapping("/student/update-self")
    public void updateStudentData() {
        WebClient webClient = WebClient.create("http://localhost:8080");

        StudentData student = new StudentData();
        webClient.post()
                .uri("/api/student/update")
                .bodyValue(student)
                .retrieve()
                .bodyToMono(StudentData.class)
                .doOnSuccess(unused -> System.out.println("Dane studenta wysłane na serwer: " + this))
                .doOnError(e -> System.err.println("Błąd podczas wysyłania danych studenta: " + e.getMessage()))
                .subscribe();
    }

     */

    @PostMapping("/student/update")
    public void updateStudentDataOnServer(@RequestBody StudentData studentData) {
        FAKE_DATA.stopTask(scheduler);
        if (studentData.getFirstName() != null) {
            instance.setFirstName(studentData.getFirstName());
        }
        if (studentData.getLastName() != null) {
            instance.setLastName(studentData.getLastName());
        }
        if (studentData.getEmail() != null) {
            instance.setEmail(studentData.getEmail());
        }
        if (studentData.getPhoneNumber() != null) {
            instance.setPhoneNumber(studentData.getPhoneNumber());
        }
        System.out.println(instance);
    }

    @GetMapping("/student/data")
    public StudentData getStudentData() {
        return instance;
    }

    @PostMapping("/enrollment/remove")
    public void removeEnrollmentById(@RequestBody int enrollmentId) {
        instance.getEnrollments().removeIf(enrollment -> enrollment.getId() == enrollmentId);
    }

    @GetMapping("/enrollment/removed/{id}")
    public int notifyEnrollmentRemoval(@PathVariable int id) {
        return id;
    }

    @GetMapping("/courses")
    public List<CourseEntity> getServerCourses() {

        List<Integer> enrolledCourseIds = instance.getEnrollments().stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        return serverCourses.stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .toList();
    }

    @PostMapping("/courses/register")
    public String registerForCourse(@RequestBody int courseId) {
        CourseEntity selectedCourse = serverCourses.stream()
                .filter(course -> course.getId() == courseId)
                .findFirst()
                .orElse(null);

        if (selectedCourse != null) {
            boolean success = instance.addNewEnrollment(selectedCourse);
            if(success){
                return "Successfully registered for course: " + selectedCourse.getName();
            }else{
                return "Nima zapisu";
            }

        } else {
            return "Course with ID " + courseId + " not found.";
        }
    }
}
