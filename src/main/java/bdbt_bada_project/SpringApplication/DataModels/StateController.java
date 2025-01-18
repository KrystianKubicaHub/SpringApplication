package bdbt_bada_project.SpringApplication.DataModels;

import bdbt_bada_project.SpringApplication.FAKE_DATA;
import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import bdbt_bada_project.SpringApplication.entities.FieldOfStudyEntity;
import bdbt_bada_project.SpringApplication.entities.PersonEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class StateController {


    private static final StudentData instance = new StudentData();
    private static final Logger log = LogManager.getLogger(StateController.class);
    private final ScheduledExecutorService scheduler;

    public StateController() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        FAKE_DATA.startUpdatingTask(scheduler, instance);
    }


    @PostMapping("/student/update-self")
    public void updateStudentData() {
        WebClient webClient = WebClient.create("http://localhost:8080");

        PersonEntity person = new PersonEntity(1,"","","","","");
        webClient.post()
                .uri("/api/student/update")
                .bodyValue(person)
                .retrieve()
                .bodyToMono(PersonEntity.class)
                .doOnSuccess(unused -> System.out.println("Dane studenta wysłane na serwer: " + this))
                .doOnError(e -> System.err.println("Błąd podczas wysyłania danych studenta: " + e.getMessage()))
                .subscribe();
    }

    @PostMapping("/student/update")
    public void updateStudentDataOnServer(@RequestBody StateController studentData) {
        StateController instance = studentData; // Zaktualizuj bieżące dane
        System.out.println("Dane studenta zaktualizowane na serwerze: " + studentData);
    }
    @GetMapping("/student/data")
    public StudentData getStudentData() {
        return instance;
    }
}
