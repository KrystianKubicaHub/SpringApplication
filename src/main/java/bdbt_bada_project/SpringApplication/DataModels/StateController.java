package bdbt_bada_project.SpringApplication.DataModels;

import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import bdbt_bada_project.SpringApplication.entities.FieldOfStudyEntity;
import bdbt_bada_project.SpringApplication.entities.PersonEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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


    private static volatile StudentData instance = new StudentData();
    private final ScheduledExecutorService scheduler;

    public StateController() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startUpdatingTask();
    }




    private void startUpdatingTask() {


        scheduler.scheduleAtFixedRate(() -> {
            Random random = new Random();


            String randomFirstName = "FirstName" + random.nextInt(1000);
            String randomLastName = "LastName" + random.nextInt(1000);
            String randomPESEL = String.valueOf(10000000000L + random.nextInt(899999999));
            String randomEmail = "email" + random.nextInt(1000) + "@example.com";
            String randomPhoneNumber = String.valueOf(500000000 + random.nextInt(499999999));


            instance.updateFirstName(randomFirstName);
            instance.updateLastName(randomLastName);
            instance.PESELNumber = randomPESEL;
            instance.updateEmail(randomEmail);
            instance.updatePhoneNumber(randomPhoneNumber);


        }, 0, 4, TimeUnit.SECONDS);
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
        Random rand = new Random();
        int wylos = rand.nextInt(0, 100);
        System.out.println("Endpoint /api/student/data został wywołany");
        PersonEntity person = new PersonEntity(wylos,"Krystiabn",""+wylos,"","","");
        return instance;
    }
}
