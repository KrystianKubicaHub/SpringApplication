document.addEventListener('DOMContentLoaded', () => {
    const buttonShowMoreOrLess = document.getElementById('toggle-info-btn');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');
    const buttonAddSubject = document.getElementById('add-subject-btn');

    if (buttonShowMoreOrLess&&buttonEdit&&buttonCancel&&buttonAddSubject) {
        buttonShowMoreOrLess.addEventListener('click', showMoreOrLess);
        buttonEdit.addEventListener('click', enableAndConfirmEditAction);
        buttonCancel.addEventListener('click', cancelEdit)
        buttonAddSubject.addEventListener('click', addSubjectButtonClicked)


        setInterval(fetchStudentData, 500);
    } else {
        console.error('chuj wie gdzie jest element');
    }
});

function enableAndConfirmEditAction() {
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

        const newStudentData = {
            id: null,
            firstName: null,
            lastName: null,
            PESELNumber: null,
            email: null,
            phoneNumber: null,
            indexNumber: null,
            studySince: null,
            totalECTS: null,
            fieldOfStudy: [],
            enrollments: [],
        };


        fields.forEach(field => {
            const input = field.querySelector('input');
            if (input) {
                if(input.value !== input.defaultValue){
                    switch (input.id) {
                        case 'studentFirstName':
                            newStudentData.firstName = input.value;
                            break;
                        case 'studentLastName':
                            newStudentData.lastName = input.value;
                            break;
                        case 'studentEmail':
                            newStudentData.email = input.value;
                            break;
                        case 'studentPhoneNumber':
                            newStudentData.phoneNumber = input.value;
                            break;
                        default:
                            console.warn(`Nieznane pole: ${input.id}`);
                            break;
                    }
                }
                const span = document.createElement('span');
                span.textContent = input.value;
                span.id = input.id;
                input.replaceWith(span);
            }
        });
        if(newStudentData.firstName !== null || newStudentData.lastName !== null || newStudentData.email !== null ||newStudentData.phoneNumber !== null){
            updateStudentData(newStudentData)
        }
    }
}

function cancelEdit() {
    const adminNote = document.getElementById('admin-note');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');
    const fields = document.querySelectorAll('#user-info p');

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

function addSubjectButtonClicked(){

    const rightPanel = document.querySelector('.right-panel');

    if (rightPanel.classList.contains('hidden')) {
        console.log('Log1')
        rightPanel.classList.remove('hidden'); // Pokazuje sekcję
    } else {
        console.log('Log2')
        rightPanel.classList.add('hidden'); // Ukrywa sekcję
    }
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

function updateEnrollments(student) {
    const enrollmentsContainer = document.getElementById('enrollments-container');

    enrollmentsContainer.innerHTML = '';

    if(student.enrollments.length !== 0){
        (student.enrollments || []).forEach((enrollment) => {

            const enrollmentDiv = document.createElement('div');
            enrollmentDiv.className = 'enrollment';
            enrollmentDiv.style.position = 'relative';

            const resignButton = document.createElement('button');
            resignButton.className = 'resign-btn';
            resignButton.textContent = 'Resign';
            resignButton.onclick = () => removeEnrollmentById(enrollment.id);

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
    }else{
        const emptyMessage = document.createElement('p');
        emptyMessage.textContent = 'You ain\'t got no courses, probably \'cause you missed registering for them. Figures.';
        emptyMessage.className = 'empty-message';
        enrollmentsContainer.appendChild(emptyMessage);
    }

}





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

function removeEnrollmentById(enrollmentId) {
    const endpoint = `/api/enrollment/remove`;

    fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(enrollmentId)
    })
        .then(response => {
            if (!response.ok){
                console.error(`Failed to remove enrollment with ID ${enrollmentId}.`);
            }
        })
        .catch(error => {
            console.error('Error while removing enrollment:', error);
        });
}



