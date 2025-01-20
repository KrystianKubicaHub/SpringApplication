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

function resetToInitialState(button) {
    const contentArea = document.querySelector('.content-area');
    const buttonsContainer = document.querySelector('.buttons-container');

    // Przywracanie wyglądu przycisku
    if (button) {
        button.querySelector('span').textContent = button.getAttribute('data-original-text');
        button.querySelector('.icon').textContent = button.getAttribute('data-original-icon');
        button.style.backgroundColor = '#4CAF50'; // Zielony
    }

    // Przywracanie kontenera przycisków do pierwotnego stanu
    if (buttonsContainer) {
        buttonsContainer.classList.remove('sticky');
    }

    // Czyszczenie panelu zawartości
    if (contentArea) {
        contentArea.innerHTML = '';
    }

    // Resetowanie globalnej zmiennej
    if (typeof activeOption !== 'undefined') {
        activeOption = '';
    }
}


document.addEventListener('DOMContentLoaded', () => {
    const addPersonButton = document.getElementById('add-person-btn');
    const showPeopleButton = document.getElementById('show-people-btn');
    const academyDetailsButton = document.getElementById('academy-details-btn');
    const showCoursesButton = document.getElementById('show-courses-btn');
    const addCourseButton = document.getElementById('add-course-btn');
    const buttonsContainer = document.querySelector('.buttons-container');
    const buttons = document.querySelectorAll('.large-btn');

    let activeOption = '';
    console.log(
        'activeOption',
        activeOption
    )

    if (!buttonsContainer || !buttons.length) {
        console.error('Buttons container or buttons not found');
        return;
    }
    buttons.forEach(button => {
        button.addEventListener('click', () => {
            buttonsContainer.classList.add('sticky');
        });
    });
    function handleOptionSelection(button, option, callback) {
        const contentArea = document.querySelector('.content-area');

        if (activeOption !== option) {
            activeOption = option;

            button.querySelector('span').textContent = 'Cancel';
            button.querySelector('.icon').textContent = '❌';
            button.style.backgroundColor = '#FFC107';

            buttonsContainer.classList.add('sticky');

            // Wywołanie odpowiedniej funkcji
            callback();
        } else {
            activeOption = '';

            // Przywracamy wygląd przycisku
            button.querySelector('span').textContent = button.getAttribute('data-original-text');
            button.querySelector('.icon').textContent = button.getAttribute('data-original-icon');
            button.style.backgroundColor = '#4CAF50';

            buttonsContainer.classList.remove('sticky');

            if (contentArea) {
                contentArea.innerHTML = '';
            }
        }
    }
    const initializeButton = (button, option, callback) => {
        const text = button.querySelector('span').textContent;
        const icon = button.querySelector('.icon').textContent;
        button.setAttribute('data-original-text', text);
        button.setAttribute('data-original-icon', icon);

        button.addEventListener('click', () => handleOptionSelection(button, option, callback));
    };

    if (addPersonButton) {
        initializeButton(addPersonButton, 'addPerson', addPersonClicked);
    }

    if (showPeopleButton) {
        initializeButton(showPeopleButton, 'showPeople', showPeopleClicked);
    }

    if (academyDetailsButton) {
        initializeButton(academyDetailsButton, 'academyDetails', academyDetailsClicked);
    }

    if (showCoursesButton) {
        initializeButton(showCoursesButton, 'showCourses', showCoursesClicked);
    }

    if (addCourseButton) {
        initializeButton(addCourseButton, 'addCourse', addCourseClicked);
    }
});