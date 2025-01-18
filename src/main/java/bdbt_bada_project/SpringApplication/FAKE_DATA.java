package bdbt_bada_project.SpringApplication;
import bdbt_bada_project.SpringApplication.DataModels.StudentData;
import bdbt_bada_project.SpringApplication.entities.CourseEntity;
import bdbt_bada_project.SpringApplication.entities.EnrollmentEntity;
import bdbt_bada_project.SpringApplication.entities.FieldOfStudyEntity;
import bdbt_bada_project.SpringApplication.entities.LecturerEntity;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FAKE_DATA {

    public static List<EnrollmentEntity> getAllEnrollments() {
        List<LecturerEntity> generatedLecturers = generateLecturers();
        List<CourseEntity> courses = new ArrayList<>();
        List<EnrollmentEntity> enrollments = new ArrayList<>();

        String[] courseNames = {
                "Anatomy", "Physiology", "Pharmacology", "Pathology",
                "Microbiology", "Immunology", "Surgery Basics", "Clinical Medicine",
                "Medical Ethics", "Radiology", "Pediatrics", "Cardiology",
                "Neurology", "Emergency Medicine"
        };

        Random random = new Random();
        for (int i = 0; i < courseNames.length; i++) {

            CourseEntity course = new CourseEntity(i,
                    courseNames[i],
                    "Course about " + courseNames[i],
                    (random.nextInt(8) + 1),
                    generatedLecturers.get(random.nextInt( generatedLecturers.size()))

            );

            courses.add(course);
        }

        for (int i = 1; i <= 50; i++) {
            CourseEntity course = courses.get(i % courses.size());
            EnrollmentEntity enrollment = new EnrollmentEntity(course, new Date());
            enrollments.add(enrollment);
        }

        return enrollments;
    }

    public static void setPersonsData(StudentData studentData) {
        FieldOfStudyEntity fose = new FieldOfStudyEntity("Tailoring");
        studentData.id = 1;
        studentData.PESELNumber = "04230809071";
        studentData.firstName = "Krystian";
        studentData.lastName = "Kubica";
        studentData.email = "krykubi@wp.pl";
        studentData.phoneNumber = "606309379";
        studentData.indexNumber = 331501;
        studentData.studySince = "2070L";
        studentData.fieldOfStudy = new ArrayList<>();
        studentData.fieldOfStudy.add(fose);
        studentData.totalECTS = 720;
    }


    public static void startUpdatingTask(ScheduledExecutorService scheduler, StudentData instance) {
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

            // Generowanie kierunków studiów
            List<String> fieldOfStudyNames = List.of(
                    "Artificial Intelligence",
                    "Quantum Computing",
                    "Space Engineering",
                    "Cybersecurity",
                    "Biomedical Sciences",
                    "Robotics and Automation",
                    "Environmental Science",
                    "Data Science"
            );
            // Czyszczenie istniejącej listy kierunków
            instance.fieldOfStudy.clear();

            System.out.println("A czy tegho printa zobacze?2: Nie");


            // Losowanie liczby kierunków studiów (1-4)
            int numberOfFields = 1 + random.nextInt(3);
            for (int i = 0; i < numberOfFields; i++) {
                String fieldName = fieldOfStudyNames.get(random.nextInt(fieldOfStudyNames.size()));
                instance.fieldOfStudy.add(new FieldOfStudyEntity(fieldName));
            }


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


    public static List<LecturerEntity> generateLecturers() {
        List<LecturerEntity> lecturers = new ArrayList<>();

        // Przykładowe dane dla wykładowców
        String[] firstNames = {"John", "Jane", "Michael", "Emily", "Robert", "Anna", "David", "Sophia", "Mark", "Olivia"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Martinez", "Hernandez", "Lopez", "Wilson"};
        String[] academicTitles = {"Professor", "Associate Professor", "Assistant Professor", "Dr.", "PhD"};
        String[] specializations = {"Mathematics", "Computer Science", "Physics", "Biology", "Chemistry", "Medicine", "Engineering", "Economics", "History", "Linguistics"};

        for (int i = 1; i <= 20; i++) {
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[i % lastNames.length];
            String academicTitle = academicTitles[i % academicTitles.length];
            String specialization = specializations[i % specializations.length];
            String pesel = String.format("990101%05d", i); // Przykładowy PESEL
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@university.com";
            String phoneNumber = "123-456-" + String.format("%04d", i);

            // Tworzenie obiektu LecturerEntity
            LecturerEntity lecturer = new LecturerEntity(
                    i, // ID
                    firstName,
                    lastName,
                    pesel,
                    email,
                    phoneNumber,
                    academicTitle,
                    specialization
            );

            // Dodanie wykładowcy do listy
            lecturers.add(lecturer);
        }

        return lecturers;
    }
}
