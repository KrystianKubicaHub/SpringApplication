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


    private static final StudentData instance = new StudentData();
    private final ScheduledExecutorService scheduler;

    public StateController() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startUpdatingTask();
    }




    private void startUpdatingTask() {
        scheduler.scheduleAtFixedRate(() -> {
            Random random = new Random();

            // Listy imion i nazwisk
            List<String> firstNames = List.of("John", "Jane", "Michael", "Emily", "Robert", "Olivia", "Daniel", "Sophia");
            List<String> lastNames = List.of("Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia");

            // Losowanie imienia i nazwiska
            String randomFirstName = firstNames.get(random.nextInt(firstNames.size()));
            String randomLastName = lastNames.get(random.nextInt(lastNames.size()));

            // Generowanie emaila
            String randomEmail = randomFirstName.toLowerCase() + "." + randomLastName.toLowerCase() + "@example.com";

            // Losowy PESEL (11 cyfr)
            String randomPESEL = String.valueOf(10000000000L + random.nextLong(90000000000L));

            // Losowy indexNumber (np. 6 cyfr)
            int randomIndexNumber = 100000 + random.nextInt(900000);

            // Losowy telefon (np. w zakresie 500000000 - 999999999)
            String randomPhoneNumber = String.valueOf(500000000 + random.nextInt(499999999));

            // Losowa liczba ECTS (np. 0 - 300)
            int randomTotalECTS = random.nextInt(301);

            // Losowanie roku rozpoczęcia studiów (np. 2000 - 2025)
            String randomStudySince = String.valueOf(2000 + random.nextInt(26));

            // Aktualizowanie pól klasy
            instance.updateFirstName(randomFirstName);
            instance.updateLastName(randomLastName);
            instance.PESELNumber = randomPESEL;
            instance.updateEmail(randomEmail);
            instance.updatePhoneNumber(randomPhoneNumber);
            instance.indexNumber = randomIndexNumber;
            instance.totalECTS = randomTotalECTS;
            instance.studySince = randomStudySince;

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
        return instance;
    }
}
