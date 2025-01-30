function updateView(student, userId) {
    const studentFirstName = document.getElementById('studentFirstName');
    const studentLastName = document.getElementById('studentLastName');
    const studentEmail = document.getElementById('studentEmail');
    const studentPhoneNumber = document.getElementById('studentPhoneNumber');

    // Additional
    const studentPESEL = document.getElementById('studentPESELNumber');
    const studentIndex = document.getElementById('studentIndexNumber');
    const studentStudySince = document.getElementById('studentStudySince');
    const studentTotalECTS = document.getElementById('studentTotalECTS');

    studentFirstName.textContent = student.firstName || 'Loading...';
    studentLastName.textContent = student.lastName || 'Loading...';
    studentEmail.textContent = student.email || 'Loading...';
    studentPhoneNumber.textContent = student.phoneNumber || 'Loading...';

    studentPESEL.textContent = student.PESELNumber || 'Loading...';
    studentIndex.textContent = student.indexNumber || 'Loading...';
    studentStudySince.textContent = student.studySince || 'Loading...';
    studentTotalECTS.textContent = student.totalECTS || 'Loading...';

    updateEnrollments(student, userId);
    updateFieldOfStudyForStudent(student);
}

function updateFieldOfStudyForStudent(student) {
    if (!student.fieldOfStudy || !Array.isArray(student.fieldOfStudy)) {
        console.error('fieldOfStudy is not defined or not an array');
        return;
    }

    const container = document.getElementById('field-of-study-container');
    container.innerHTML = ''; // Wyczyszczenie poprzedniej zawartości

    student.fieldOfStudy.forEach((field) => {
        const fieldOfStudyName = field.name;

        if (fieldOfStudyName) {
            // Tworzenie elementu dla kierunku studiów
            const studyElement = document.createElement('div');
            studyElement.className = 'field-of-study-item'; // Przypisanie klasy CSS
            studyElement.textContent = fieldOfStudyName;

            // Dodanie elementu do kontenera
            container.appendChild(studyElement);
        }
    });
}

function updateEnrollments(student, userId) {
    const enrollmentsContainer = document.getElementById('enrollments-container');

    enrollmentsContainer.innerHTML = '';

    console.log(student.enrollments.length);
    if (student.enrollments.length !== 0) {
        student.enrollments.forEach((enrollment) => {
            const enrollmentId = enrollment.id; // Wyciągamy ID zapisu

            const enrollmentDiv = document.createElement('div');
            enrollmentDiv.className = 'enrollment';
            enrollmentDiv.style.position = 'relative';

            const resignButton = document.createElement('button');
            resignButton.className = 'resign-btn';
            resignButton.textContent = 'Resign';

            // Obsługa kliknięcia przycisku "Resign"
            resignButton.onclick = async () => {
                try {
                    if (!userId) {
                        console.error('User ID is missing.');
                        return;
                    }

                    console.log(`Attempting to remove enrollment: userId=${userId}, enrollmentId=${enrollmentId}`);
                    await removeEnrollmentById(userId, enrollmentId); // Przekazujemy oba argumenty
                    await fetchAndDisplayCourses(userId); // Odśwież listę kursów
                } catch (error) {
                    console.error('Error during unenrollment or course fetch:', error);
                }
            };

            // Tworzymy elementy wizualne dla kursu
            const courseName = document.createElement('h3');
            courseName.textContent = enrollment.course.name || 'No name';

            const lecturerInfo = document.createElement('p');
            lecturerInfo.className = 'Lecturer';
            lecturerInfo.textContent = `Lecturer: ${enrollment.course.lecturer.academicTitle || ''} ${enrollment.course.lecturer.firstName || ''} ${enrollment.course.lecturer.lastName || ''}`;

            const courseDescription = document.createElement('p');
            courseDescription.className = 'description';
            courseDescription.textContent = `Description: ${enrollment.course.description || 'No description'}`;

            const ectsInfo = document.createElement('p');
            ectsInfo.className = 'ects';
            ectsInfo.textContent = `ECTS Credits: ${enrollment.course.ectsCredits || 0}`;

            const enrollmentDate = document.createElement('p');
            enrollmentDate.className = 'enrollment-date';
            enrollmentDate.textContent = `Enrollment Date: ${enrollment.enrollmentDate || 'No date'}`;

            // Dodajemy elementy do kontenera przedmiotu
            enrollmentDiv.appendChild(resignButton);
            enrollmentDiv.appendChild(courseName);
            enrollmentDiv.appendChild(lecturerInfo);
            enrollmentDiv.appendChild(courseDescription);
            enrollmentDiv.appendChild(ectsInfo);
            enrollmentDiv.appendChild(enrollmentDate);

            // Dodajemy przedmiot do głównego kontenera
            enrollmentsContainer.appendChild(enrollmentDiv);
        });
    } else {
        const emptyMessage = document.createElement('p');
        emptyMessage.textContent = 'You ain\'t got no courses, probably \'cause you missed registering for them. Figures.';
        emptyMessage.className = 'empty-message';
        enrollmentsContainer.appendChild(emptyMessage);
    }
}


