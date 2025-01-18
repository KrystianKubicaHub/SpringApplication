package bdbt_bada_project.SpringApplication.DataModels;

import bdbt_bada_project.SpringApplication.FAKE_DATA;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/api")
public class StateController {

    private static final StudentData instance = new StudentData();
    private final ScheduledExecutorService scheduler;

    public StateController() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        FAKE_DATA.startUpdatingTask(scheduler, instance);
        FAKE_DATA.startRemovingEnrollmentTask(scheduler, instance);
        ///FAKE_DATA.stopUpdatingTask(scheduler);
    }


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
}
