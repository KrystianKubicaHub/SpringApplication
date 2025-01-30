function addPersonClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '';

    const header = document.createElement('h3');
    header.textContent = 'Add New Person';
    header.className = 'adp-form-header';
    contentArea.appendChild(header);

    const form = document.createElement('form');
    form.className = 'adp-add-person-form';

    // Login field
    const loginField = createInputField('Login', 'text', 'adp-login');

    // Password field
    const passwordField = createInputField('Password', 'password', 'adp-password');

    // Role selection
    const roleSelect = document.createElement('select');
    roleSelect.id = 'adp-role';
    roleSelect.className = 'adp-form-select';
    ['STUDENT', 'LECTURER', 'ADMIN'].forEach(role => {
        const option = document.createElement('option');
        option.value = role;
        option.textContent = role;
        roleSelect.appendChild(option);
    });
    const roleLabel = createLabel('Role', 'adp-role');
    form.appendChild(roleLabel);
    form.appendChild(roleSelect);

    const firstNameField = createInputField('First Name', 'text', 'adp-first-name');
    const lastNameField = createInputField('Last Name', 'text', 'adp-last-name');
    const emailField = createInputField('Email', 'email', 'adp-email');
    const phoneNumberField = createInputField('Phone Number', 'tel', 'adp-phone-number');
    const peselField = createInputField('PESEL', 'text', 'adp-pesel');
    const indexNumberField = createInputField('Index Number', 'number', 'adp-index-number');

    const submitButton = document.createElement('button');
    submitButton.type = 'button';
    submitButton.textContent = 'Add Person';
    submitButton.className = 'adp-submit-button';

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
        addNewUser(newUserRequest).then(isSuccessful => {
            if (isSuccessful) {
                resetButtonStyles();
                resetButtonsPosition();
                resetContentPanel();
            } else {
                console.log('Metoda addNewUser zwróciła: fałsz');
            }
        });


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

async function addNewUser(newUserRequest) {
    try {
        const response = await fetch('/api/admin/users/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newUserRequest)
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Failed to add user:', errorText);
            alert(`Error: ${errorText}`);
            return false;
        }

        const successMessage = await response.text();
        console.log('User added successfully:', successMessage);
        return true;
    } catch (error) {
        console.error('Error adding user:', error);
        alert('An error occurred while adding the user.');
    }
}
