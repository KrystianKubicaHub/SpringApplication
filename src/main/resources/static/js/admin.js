document.addEventListener('DOMContentLoaded', () => {
    const addPersonButton = document.getElementById('add-person-btn');
    const showPeopleButton = document.getElementById('show-people-btn');
    const academyDetailsButton = document.getElementById('academy-details-btn');
    const showCoursesButton = document.getElementById('show-courses-btn');
    const addCourseButton = document.getElementById('add-course-btn');
    const buttonsContainer = document.querySelector('.buttons-container');
    const buttons = document.querySelectorAll('.large-btn');

    if (!buttonsContainer || !buttons.length) {
        console.error('Buttons container or buttons not found');
        return;
    }

    buttons.forEach(button => {
        button.addEventListener('click', () => {
            buttonsContainer.classList.add('sticky');
        });
    });

    if (addPersonButton) {
        let activeOption = '';
        addPersonButton.addEventListener('click', () => {
            const buttonsContainer = document.querySelector('.buttons-container');
            const contentArea = document.querySelector('.content-area');

            if (activeOption !== 'addPerson') {
                activeOption = 'addPerson';

                addPersonButton.querySelector('span').textContent = 'Cancel';
                addPersonButton.querySelector('.icon').textContent = '❌';
                addPersonButton.style.backgroundColor = '#FFC107'; // Żółty kolor

                buttonsContainer.classList.add('sticky');

                addPersonClicked();
            } else {
                activeOption = '';

                addPersonButton.querySelector('span').textContent = 'Add Person';
                addPersonButton.querySelector('.icon').textContent = '👤';
                addPersonButton.style.backgroundColor = '#4CAF50';

                buttonsContainer.classList.remove('sticky');

                if (contentArea) {
                    contentArea.innerHTML = '';
                }
            }
        });
    }

    if (showPeopleButton) {
        showPeopleButton.addEventListener('click', () => {
            showPeopleClicked();
        });
    }

    if (academyDetailsButton) {
        academyDetailsButton.addEventListener('click', () => {
            academyDetailsClicked();
        });
    }

    if (showCoursesButton) {
        showCoursesButton.addEventListener('click', () => {
            showCoursesClicked();
        });
    }

    if (addCourseButton) {
        addCourseButton.addEventListener('click', () => {
            addCourseClicked();
        });
    }
});

function addPersonClicked() {
    const contentArea = document.querySelector('.content-area');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '';

    const header = document.createElement('h3');
    header.textContent = 'Add New Person';
    header.className = 'form-header';
    contentArea.appendChild(header);

    const form = document.createElement('form');
    form.className = 'add-person-form';

    // Login field
    const loginField = createInputField('Login', 'text', 'login');

    // Password field
    const passwordField = createInputField('Password', 'password', 'password');

    // Role selection
    const roleSelect = document.createElement('select');
    roleSelect.id = 'role';
    roleSelect.className = 'form-select';
    ['STUDENT', 'LECTURER', 'ADMIN'].forEach(role => {
        const option = document.createElement('option');
        option.value = role;
        option.textContent = role;
        roleSelect.appendChild(option);
    });
    const roleLabel = createLabel('Role', 'role');
    form.appendChild(roleLabel);
    form.appendChild(roleSelect);

    const firstNameField = createInputField('First Name', 'text', 'first-name');
    const lastNameField = createInputField('Last Name', 'text', 'last-name');
    const emailField = createInputField('Email', 'email', 'email');
    const phoneNumberField = createInputField('Phone Number', 'tel', 'phone-number');
    const peselField = createInputField('PESEL', 'text', 'pesel');
    const indexNumberField = createInputField('Index Number', 'number', 'index-number');


    const submitButton = document.createElement('button');
    submitButton.type = 'button';
    submitButton.textContent = 'Add Person';
    submitButton.className = 'submit-button';

    submitButton.addEventListener('click', () => {
        const newUserRequest = {
            login: loginField.querySelector('input').value,
            password: passwordField.querySelector('input').value,
            role: roleSelect.value,
            studentData: roleSelect.value === "STUDENT" ? {
                firstName: firstNameField.querySelector('input').value,
                lastName: lastNameField.querySelector('input').value,
                email: emailField.querySelector('input').value,
                phoneNumber: phoneNumberField.querySelector('input').value,
                PESELNumber: peselField.querySelector('input').value,
                indexNumber: Number(indexNumberField.querySelector('input').value),
            } : null,
        };

        if (!newUserRequest.login || !newUserRequest.password || !newUserRequest.role) {
            alert('Please fill in all required fields for login, password, and role.');
            return;
        }

        if (newUserRequest.role === "STUDENT" && (!newUserRequest.studentData.firstName ||
            !newUserRequest.studentData.lastName ||
            !newUserRequest.studentData.email ||
            !newUserRequest.studentData.phoneNumber ||
            !newUserRequest.studentData.PESELNumber ||
            !newUserRequest.studentData.indexNumber)) {
            alert('Please fill in all required fields for student data.');
            return;
        }

        console.log('New user request:', newUserRequest);
        addNewUser(newUserRequest);
    });

    form.appendChild(loginField);
    form.appendChild(passwordField);
    form.appendChild(firstNameField);
    form.appendChild(lastNameField);
    form.appendChild(emailField);
    form.appendChild(phoneNumberField);
    form.appendChild(peselField);
    form.appendChild(indexNumberField);
    form.appendChild(submitButton);

    contentArea.appendChild(form);
}

function createInputField(labelText, type, id) {
    const container = document.createElement('div');
    container.className = 'form-group';

    const label = createLabel(labelText, id);
    const input = document.createElement('input');
    input.type = type;
    input.id = id;
    input.className = 'form-input';

    container.appendChild(label);
    container.appendChild(input);

    return container;
}

function createLabel(text, forId) {
    const label = document.createElement('label');
    label.htmlFor = forId;
    label.textContent = text;
    label.className = 'form-label';
    return label;
}
