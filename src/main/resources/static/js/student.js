document.addEventListener('DOMContentLoaded', () => {
    const buttonShowMoreOrLess = document.getElementById('toggle-info-btn');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');

    if (buttonShowMoreOrLess&&buttonEdit&&buttonCancel) {
        buttonShowMoreOrLess.addEventListener('click', showMoreOrLess);
        buttonEdit.addEventListener('click', enableEdit);
        buttonCancel.addEventListener('click', cancelEdit)
    } else {
        console.error('chuj wie gdzie jest element');
    }
});

function enableEdit() {
    const adminNote = document.getElementById('admin-note');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');
    const fields = document.querySelectorAll('#user-info p');

    if (buttonEdit.textContent === 'Edit') {
        adminNote.style.display = 'block';
        buttonCancel.style.display = 'block';
        buttonEdit.textContent = 'Confirm';


        fields.forEach(field => {
            const span = field.querySelector('span');
            if (span) {
                const input = document.createElement('input');
                input.type = 'text';
                input.value = span.textContent;
                input.defaultValue = span.textContent;
                input.id = span.id;
                input.className = 'edit-input';
                span.replaceWith(input);
            }
        });
    } else {
        adminNote.style.display = 'none';
        buttonCancel.style.display = 'none';
        buttonEdit.textContent = 'Edit';

        fields.forEach(field => {
            const input = field.querySelector('input');
            if (input) {
                const span = document.createElement('span');
                span.textContent = input.value;
                span.id = input.id;
                input.replaceWith(span);
            }
        });
    }
}

function cancelEdit() {
    const adminNote = document.getElementById('admin-note');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');
    const fields = document.querySelectorAll('#user-info p');

    // Przywróć oryginalny tekst i ukryj tryb edycji
    adminNote.style.display = 'none';
    buttonCancel.style.display = 'none';
    buttonEdit.textContent = 'Edit';

    fields.forEach(field => {
        const input = field.querySelector('input');
        if (input) {
            const span = document.createElement('span');
            span.textContent = input.defaultValue; // Przywraca oryginalny tekst
            span.id = input.id;
            input.replaceWith(span);
        }
    });
}






function showMoreOrLess() {
    const additionalInfoContainer = document.getElementById('additional-info');
    const toggleButton = document.getElementById('toggle-info-btn');


    if (additionalInfoContainer.style.display === 'block') {
        additionalInfoContainer.style.display = 'none';
        toggleButton.textContent = 'Show More';
    } else {
        additionalInfoContainer.style.display = 'block';
        toggleButton.textContent = 'Show Less';
    }
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
        console.log(fieldOfStudyName);

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



function updateView(student) {
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

    updateEnrollments(student);
    updateFieldOfStudyForStudent(student);
}


async function updateStudentData(student) {
    try {
        const response = await fetch('/api/student/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(student),
        });

        if (!response.ok) {
            throw new Error(`Błąd: ${response.status}`);
        }

        console.log('Dane studenta zaktualizowane.');
    } catch (error) {
        console.error('Błąd podczas aktualizacji danych studenta:', error);
    }
}

// Odświeżaj dane co 0,5 sekund
setInterval(fetchStudentData, 500);