// Pobierz dane z backendu
async function fetchStudentData() {
    try {
        const response = await fetch('/api/student/data', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`Błąd: ${response.status}`);
        }

        const personData = await response.json();
        updateView(personData); // Aktualizuj widok HTML
    } catch (error) {
        console.error('Błąd podczas pobierania danych studenta:', error);
    }
}


function updateEnrollments(student) {
    const enrollmentsContainer = document.getElementById('enrollments-container');

    // Wyczyszczenie obecnej listy przedmiotów
    enrollmentsContainer.innerHTML = '';


    // Przechodzimy przez każdy przedmiot w liście `enrollments`
    (student.enrollments || []).forEach((enrollment) => {
        // Tworzymy kontener dla pojedynczego przedmiotu
        const enrollmentDiv = document.createElement('div');
        enrollmentDiv.className = 'enrollment';
        enrollmentDiv.style.position = 'relative';

        // Tworzymy przycisk do rezygnacji
        const resignButton = document.createElement('button');
        resignButton.className = 'resign-btn';
        resignButton.textContent = 'Resign';
        resignButton.onclick = () => confirmResignation(enrollment.course.id);

        // Tworzymy tytuł przedmiotu
        const courseName = document.createElement('h3');
        courseName.textContent = enrollment.course.name || 'Brak nazwy';

        // Tworzymy dane o wykładowcy
        const lecturerInfo = document.createElement('p');
        lecturerInfo.className = 'Lecturer';
        lecturerInfo.textContent = `Lecturer: ${enrollment.course.lecturer.academicTitle || ''} ${enrollment.course.lecturer.firstName || ''} ${enrollment.course.lecturer.lastName || ''}`;

        // Tworzymy opis przedmiotu
        const courseDescription = document.createElement('p');
        courseDescription.className = 'description';
        courseDescription.textContent = `Description: ${enrollment.course.description || 'Brak opisu'}`;

        // Tworzymy dane o punktach ECTS
        const ectsInfo = document.createElement('p');
        ectsInfo.className = 'ects';
        ectsInfo.textContent = `ECTS Credits: ${enrollment.course.ectsCredits || 0}`;

        // Tworzymy datę zapisania
        const enrollmentDate = document.createElement('p');
        enrollmentDate.className = 'enrollment-date';
        enrollmentDate.textContent = `Enrollment Date: ${enrollment.enrollmentDate || 'Brak daty'}`;

        // Dodajemy wszystkie elementy do kontenera pojedynczego przedmiotu
        enrollmentDiv.appendChild(resignButton);
        enrollmentDiv.appendChild(courseName);
        enrollmentDiv.appendChild(lecturerInfo);
        enrollmentDiv.appendChild(courseDescription);
        enrollmentDiv.appendChild(ectsInfo);
        enrollmentDiv.appendChild(enrollmentDate);

        // Dodajemy kontener przedmiotu do głównego kontenera
        enrollmentsContainer.appendChild(enrollmentDiv);
    });
}


function updateView(person) {
    const studentFirstName = document.getElementById('studentFirstName');
    const studentLastName = document.getElementById('studentLastName');
    const studentEmail = document.getElementById('studentEmail');
    const studentPhoneNumber = document.getElementById('studentPhoneNumber');

    studentFirstName.textContent = person.firstName || 'Loading...';
    studentLastName.textContent = person.lastName || 'Loading...';
    studentEmail.textContent = person.email || 'Loading...';
    studentPhoneNumber.textContent = person.phoneNumber || 'Loading...';
    updateEnrollments(person);
}

// Funkcja wywoływana przez backend przy zmianach danych
async function updateStudentData(person) {
    try {
        const response = await fetch('/api/student/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(person),
        });

        if (!response.ok) {
            throw new Error(`Błąd: ${response.status}`);
        }

        console.log('Dane studenta zaktualizowane.');
    } catch (error) {
        console.error('Błąd podczas aktualizacji danych studenta:', error);
    }
}

// Odświeżaj dane co 5 sekund
setInterval(fetchStudentData, 500);