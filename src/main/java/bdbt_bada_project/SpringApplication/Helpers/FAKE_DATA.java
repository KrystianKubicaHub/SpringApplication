package bdbt_bada_project.SpringApplication.Helpers;
import bdbt_bada_project.SpringApplication.Persistence.GlobalDataManager;
import bdbt_bada_project.SpringApplication.Controllers.UserSessionController;
import bdbt_bada_project.SpringApplication.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FAKE_DATA {

    public static int numberOfStudents = 20;


    public static List<EnrollmentEntity> generateEnrollments() {
        List<LecturerEntity> generatedLecturers = generateLecturers();
        List<EnrollmentEntity> enrollments = new ArrayList<>();

        String[] courseNames = {
                "Anatomy", "Physiology", "Pharmacology", "Pathology",
                "Microbiology", "Immunology", "Surgery Basics", "Clinical Medicine"
        };

        Random random = new Random();
        for (int i = 0; i < courseNames.length; i++) {

            CourseEntity course = new CourseEntity(i,
                    courseNames[i],
                    "Course about " + courseNames[i],
                    (random.nextInt(8) + 1),
                    generatedLecturers.get(random.nextInt(generatedLecturers.size()))
            );
            EnrollmentEntity enrollment = new EnrollmentEntity(course, new Date(), i);
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

    public static SerializationResult isSerializable(Object ob) {
        if (ob == null) {
            return new SerializationResult(false, "Object is null");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValueAsString(ob);
            return new SerializationResult(true, null); // Sukces, brak błędu
        } catch (Exception e) {
            return new SerializationResult(false, e.getMessage()); // Niepowodzenie, zwracamy komunikat błędu
        }
    }


    public static List<CourseEntity> generateCourses(int numberOfCourses) {
        List<String> courseNames = List.of(
                "Image Analysis in Caravaggio's Paintings",
                "The Literary Depiction of Death in Concentration Camps",
                "Impact of Classical Music on 18th Century Geopolitics",
                "Symbolism in Romantic Landscape Paintings",
                "Analyzing the Role of Shadows in Baroque Art",
                "The Use of Imagery in Modernist Poetry",
                "Influence of Ancient Greek Myths on Renaissance Art",
                "Artistic Responses to the Industrial Revolution",
                "The Evolution of Abstract Art in the 20th Century",
                "Interpreting the Sublime in Gothic Literature",
                "The Power of Visual Rhetoric in Political Cartoons",
                "Analyzing Propaganda Posters from World War II",
                "Music and Identity in Post-Colonial Societies",
                "Gender Representation in Victorian Literature",
                "The Interplay of Text and Image in Graphic Novels",
                "Representation of War in Impressionist Paintings",
                "Exploring Light and Color in Impressionism",
                "Dystopian Themes in Contemporary Cinema",
                "Art and Technology: The Rise of Digital Media",
                "The Literary Influence of Historical Traumas"
        );

        Random random = new Random();
        List<LecturerEntity> lecturers = FAKE_DATA.generateLecturers();
        List<CourseEntity> courses = new ArrayList<>();

        for (int i = 0; i < numberOfCourses; i++) {
            LecturerEntity lecturer = lecturers.get(random.nextInt(lecturers.size()));

            String courseName = courseNames.get(i % courseNames.size()); // Rotacja po liście nazw
            String description = "Course focusing on " + courseName.toLowerCase();
            int ectsCredits = random.nextInt(5) + 1; // ECTS od 1 do 5

            CourseEntity course = new CourseEntity(
                    i + 1, // ID kursu
                    courseName,
                    description,
                    ectsCredits,
                    lecturer
            );

            courses.add(course);
        }

        return courses;
    }

    public static void startUpdatingTask(ScheduledExecutorService scheduler, StudentData instance) {
        scheduler.scheduleAtFixedRate(() -> {
            Random random = new Random();
            System.out.println("task działa" + System.currentTimeMillis());
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


            // Losowanie liczby kierunków studiów (1-4)
            int numberOfFields = 1 + random.nextInt(3);
            for (int i = 0; i < numberOfFields; i++) {
                String fieldName = fieldOfStudyNames.get(random.nextInt(fieldOfStudyNames.size()));
                instance.fieldOfStudy.add(new FieldOfStudyEntity(fieldName));
            }
            instance.setFirstName(randomFirstName);
            instance.setLastName(randomLastName);
            instance.PESELNumber = randomPESEL;
            instance.setEmail(randomEmail);
            instance.setPhoneNumber(randomPhoneNumber);
            instance.indexNumber = randomIndexNumber;
            instance.totalECTS = randomTotalECTS;
            instance.studySince = randomStudySince;

        }, 0, 4, TimeUnit.SECONDS);
    }

    public static void startRemovingEnrollmentTask(ScheduledExecutorService taskExecutor, StudentData studentData) {
        taskExecutor.scheduleAtFixedRate(() -> {
            if (!studentData.removeEnrollmentByIndex(0)) {
                //taskExecutor.shutdown();
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    public static void stopTask(ScheduledExecutorService scheduler) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        } else {
            System.out.println("Zadanie już zostało zatrzymane lub scheduler jest null.");
        }
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

    public static StudentData createExampleStudent() {
        StudentData exampleStudent = new StudentData();

        exampleStudent.setFirstName("Alexis");
        exampleStudent.setLastName("Valentine");
        exampleStudent.PESELNumber = "92110312345";
        exampleStudent.setEmail("alexis.valentine@example.com");
        exampleStudent.setPhoneNumber("789456123");
        exampleStudent.indexNumber = 654321;
        exampleStudent.studySince = "2021";
        exampleStudent.totalECTS = 180;

        FieldOfStudyEntity primaryField = new FieldOfStudyEntity("Art History");
        FieldOfStudyEntity secondaryField = new FieldOfStudyEntity("Fashion Design");
        exampleStudent.fieldOfStudy.add(primaryField);
        exampleStudent.fieldOfStudy.add(secondaryField);

        CourseEntity course1 = new CourseEntity(
                101,
                "History of Modern Art",
                "Exploration of key movements and figures in modern art.",
                6,
                new LecturerEntity(1, "Sophia", "Marquez", "92110333345", "s.marquez@university.com", "123-456-789", "Professor", "Art History")
        );

        CourseEntity course2 = new CourseEntity(
                102,
                "Luxury Brand Strategy",
                "Understanding the psychology behind high-end fashion brands.",
                5,
                new LecturerEntity(2, "Julian", "Royce", "88050555512", "j.royce@university.com", "987-654-321", "Associate Professor", "Fashion Business")
        );

        exampleStudent.getEnrollments().add(new EnrollmentEntity(course1, new Date(), 1));
        exampleStudent.getEnrollments().add(new EnrollmentEntity(course2, new Date(), 2));

        return exampleStudent;
    }

    public static List<UserSessionController.UserAccount> getAccountsCredentialsFromSQL(int countPerRole) {
        List<UserSessionController.UserAccount> accounts = new ArrayList<>();

        for (int i = 1; i <= countPerRole; i++) {
            accounts.add(new UserSessionController.UserAccount(
                    i,
                    "student" + i,
                    "pass",
                    UserSessionController.UserRole.STUDENT
            ));
        }

        for (int i = 1; i <= countPerRole; i++) {
            accounts.add(new UserSessionController.UserAccount(
                    countPerRole + i,
                    "lecturer" + i,
                    "pass",
                    UserSessionController.UserRole.LECTURER
            ));
        }


        for (int i = 1; i <= countPerRole; i++) {
            accounts.add(new UserSessionController.UserAccount(
                    countPerRole * 2 + i,
                    "admin" + i,
                    "admin",
                    UserSessionController.UserRole.ADMIN
            ));
        }

        return accounts;
    }


    public static Map<Integer, StudentData> generateStudentDataEntries(List<UserSessionController.UserAccount> userAccounts) {
        Map<Integer, StudentData> studentDataMap = new HashMap<>();

        for (UserSessionController.UserAccount account : userAccounts) {
            if (account.getRole() == UserSessionController.UserRole.STUDENT) {
                StudentData studentData = new StudentData();

                studentData.id = account.getId();
                studentData.PESELNumber = generateRandomPESEL();
                studentData.firstName = generateRandomFirstName();
                studentData.lastName = generateRandomLastName();
                studentData.email = generateEmailForStudent(studentData.firstName, studentData.lastName);
                studentData.phoneNumber = generateRandomPhoneNumber();
                studentData.indexNumber = generateIndexNumber(account.getId());
                studentData.studySince = generateRandomStudySince();
                studentData.totalECTS = 0;

                FieldOfStudyEntity fose = new FieldOfStudyEntity(generateRandomFieldOfStudy());
                studentData.fieldOfStudy = new ArrayList<>();
                studentData.fieldOfStudy.add(fose);

                studentDataMap.put(studentData.id, studentData);
            }
        }

        return studentDataMap;
    }

    public static Map<Integer, LecturerEntity> generateLecturerDataEntries(List<UserSessionController.UserAccount> userAccounts) {
        Map<Integer, LecturerEntity> lecturerDataMap = new HashMap<>();

        for (UserSessionController.UserAccount account : userAccounts) {
            if (account.getRole() == UserSessionController.UserRole.LECTURER) {
                String firstName = generateRandomFirstName();
                String lastName = generateRandomLastName();
                LecturerEntity lecturerData = new LecturerEntity(
                        account.getId(),
                        firstName,
                        lastName,
                        generateRandomPESEL(),
                        generateEmailForLecturer(firstName, lastName),
                        generateRandomPhoneNumber(),
                        generateRandomAcademicTitle(),
                        generateRandomSpecialization()
                );

                lecturerDataMap.put(lecturerData.getId(), lecturerData);
            }
        }

        return lecturerDataMap;
    }




    private static String generateRandomPESEL() {
        return "042308" + (int) (Math.random() * 1000000); // Przykładowy PESEL
    }

    private static String generateRandomFirstName() {
        String[] firstNames = {"Krystian", "Anna", "Michał", "Karolina", "Tomasz"};
        return firstNames[(int) (Math.random() * firstNames.length)];
    }

    private static String generateRandomLastName() {
        String[] lastNames = {"Kubica", "Nowak", "Kowalski", "Wiśniewski", "Zieliński"};
        return lastNames[(int) (Math.random() * lastNames.length)];
    }

    private static String generateEmailForStudent(String firstName, String lastName) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@student.university.edu";
    }

    private static String generateRandomPhoneNumber() {
        return "600" + (int) (Math.random() * 1000000);
    }

    private static int generateIndexNumber(int id) {
        return 100000 + id; // Indeks bazujący na ID użytkownika
    }

    private static String generateRandomStudySince() {
        String[] semesters = {"2021L", "2022L", "2023L", "2024L"};
        return semesters[(int) (Math.random() * semesters.length)];
    }

    private static String generateRandomFieldOfStudy() {
        String[] fields = {"Computer Science", "Mathematics", "Physics", "Biology", "Tailoring"};
        return fields[(int) (Math.random() * fields.length)];
    }

    public static AcademyEntity loadFromSQLAcademyEntity() {
        AddressEntity address = new AddressEntity(
                1,
                "Main Street",
                "12",
                null,
                "Zurich",
                "8001",
                "Switzerland"
        );
        LecturerEntity dean = new LecturerEntity(
                12,
                "John",
                "Doe",
                "87012345678",
                "john.doe@zurichmedicalacademy.ch",
                "+41-44-765-4321",
                "Professor",
                "Surgery"
        );

        return new AcademyEntity(
                1,
                "Zurich Medical Academy",
                "+41-44-123-4567",
                "info@zurichmedicalacademy.ch",
                address,
                dean
        );
    }

    private static final List<String> ACADEMIC_TITLES = Arrays.asList("Professor", "Associate Professor", "Assistant Professor", "Dr.", "PhD");

    public static String generateRandomAcademicTitle() {
        return ACADEMIC_TITLES.get(new Random().nextInt(ACADEMIC_TITLES.size()));
    }

    private static final List<String> SPECIALIZATIONS = Arrays.asList("Mathematics", "Computer Science", "Physics", "Chemistry", "Biology", "Engineering");

    public static String generateRandomSpecialization() {
        return SPECIALIZATIONS.get(new Random().nextInt(SPECIALIZATIONS.size()));
    }

    public static String generateEmailForLecturer(String firstName, String lastName) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@university.edu";
    }

}
