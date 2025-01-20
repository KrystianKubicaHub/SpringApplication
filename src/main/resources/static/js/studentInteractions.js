document.addEventListener('DOMContentLoaded', () => {
    const userId = getUserIdFromSession();
    if (!userId) {
        console.error("User ID not found in session. Redirecting to login...");
        window.location.href = '/login';
        return;
    }else {
        console.log("User ID found in session. ${userId}");
    }


    const buttonShowMoreOrLess = document.getElementById('toggle-info-btn');
    const buttonEdit = document.getElementById('edit-btn');
    const buttonCancel = document.getElementById('cancel-btn');
    const buttonAddSubject = document.getElementById('add-subject-btn');
    const coursesPanel = document.querySelector('.courses-panel');

    if (buttonShowMoreOrLess && buttonEdit && buttonCancel && buttonAddSubject) {
        buttonShowMoreOrLess.addEventListener('click', showMoreOrLess);
        buttonEdit.addEventListener('click', () => enableAndConfirmEditAction(userId));
        buttonCancel.addEventListener('click', cancelEdit);
        buttonAddSubject.addEventListener('click', () => addSubjectButtonClicked(userId));
        coursesPanel.classList.add('hidden');

        setInterval(() => fetchStudentData(userId), 500);
    } else {
        console.error('Required elements not found on the page.');
    }
});


function enableAndConfirmEditAction(userId) {
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
            id: userId,
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
                if (input.value !== input.defaultValue) {
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
                            console.warn(`Unknown field: ${input.id}`);
                            break;
                    }
                }
                const span = document.createElement('span');
                span.textContent = input.value;
                span.id = input.id;
                input.replaceWith(span);
            }
        });

        if (newStudentData.firstName || newStudentData.lastName || newStudentData.email || newStudentData.phoneNumber) {
            updateStudentData(userId, newStudentData);
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


function getUserIdFromSession() {
    return localStorage.getItem('userId');
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


function addSubjectButtonClicked(userId) {
    const rightPanel = document.querySelector('.right-panel');
    const coursesPanel = document.querySelector('.courses-panel');
    const addSubjectButton = document.getElementById('add-subject-btn');

    if (rightPanel.classList.contains('hidden')) {
        rightPanel.classList.remove('hidden');
        coursesPanel.classList.add('hidden');
        addSubjectButton.innerHTML = '<span class="add-subject-icon">+</span> Enroll';
    } else {
        fetchAndDisplayCourses(userId);
        rightPanel.classList.add('hidden');
        coursesPanel.classList.remove('hidden');
        addSubjectButton.innerHTML = '<span class="add-subject-icon">&larr;</span> Back';
    }
}

