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
            return;
        }

        const successMessage = await response.text();
        const buttonb1 = document.getElementById("add-person-btn");
        console.log('User added successfully:', successMessage);
        handleOptionSelection(buttonb1)
        activeOption = '';
    } catch (error) {
        console.error('Error adding user:', error);
        alert('An error occurred while adding the user.');
    }
}

async function showPeopleClicked() {
    const contentArea = document.querySelector('.content-area');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '<h3 class="loading-message">Loading users...</h3>';

    try {
        const response = await fetch('/api/admin/users', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch users. Status: ${response.status}`);
        }

        const users = await response.json();

        contentArea.innerHTML = ''; // Clear the loading message

        if (users.length === 0) {
            contentArea.innerHTML = '<p class="no-users-message">No users found.</p>';
            return;
        }

        const usersTable = document.createElement('table');
        usersTable.className = 'users-table';

        // Table header
        const headerRow = document.createElement('tr');
        headerRow.innerHTML = `
            <th>ID</th>
            <th>Login</th>
            <th>Role</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Phone Number</th>
            <th>PESEL</th>
            <th>Index Number</th>
            <th>Actions</th>
        `;
        usersTable.appendChild(headerRow);

        // Populate table rows
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.login}</td>
                <td>${user.role}</td>
                <td>${user.studentData?.firstName || 'N/A'}</td>
                <td>${user.studentData?.lastName || 'N/A'}</td>
                <td>${user.studentData?.email || 'N/A'}</td>
                <td>${user.studentData?.phoneNumber || 'N/A'}</td>
                <td>${user.studentData?.PESELNumber || 'N/A'}</td>
                <td>${user.studentData?.indexNumber || 'N/A'}</td>
                <td>
                    <button class="action-btn show-more-btn" data-user-id="${user.id}">Show More</button>
                    <button class="action-btn edit-btn" data-user-id="${user.id}">Edit</button>
                    <button class="action-btn delete-btn" data-user-id="${user.id}">Delete</button>
                </td>
            `;
            usersTable.appendChild(row);
        });

        contentArea.appendChild(usersTable);

        // Add event listeners for actions
        const showMoreButtons = document.querySelectorAll('.show-more-btn');
        const editButtons = document.querySelectorAll('.edit-btn');
        const deleteButtons = document.querySelectorAll('.delete-btn');

        showMoreButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                const userId = event.target.getAttribute('data-user-id');
                console.log(`Show More clicked for user ID: ${userId}`);
                // Implement show more functionality here
            });
        });

        editButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                const userId = event.target.getAttribute('data-user-id');
                console.log(`Edit clicked for user ID: ${userId}`);
                // Implement edit functionality here
            });
        });

        deleteButtons.forEach(button => {
            button.addEventListener('click', async (event) => {
                const userId = event.target.getAttribute('data-user-id');
                console.log(`Delete clicked for user ID: ${userId}`);
                const confirmDelete = confirm(`Are you sure you want to delete user ID: ${userId}?`);
                if (confirmDelete) {
                    await deleteUser(userId);
                    showPeopleClicked(); // Refresh the table after deletion
                }
            });
        });

    } catch (error) {
        console.error('Error fetching users:', error);
        contentArea.innerHTML = '<p class="error-message">Failed to load users. Please try again later.</p>';
    }
}

async function deleteUser(userId) {
    try {
        const response = await fetch(`/api/admin/users/delete?userId=${userId}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        console.log(`User ID ${userId} deleted successfully.`);
    } catch (error) {
        console.error(`Error deleting user ID ${userId}:`, error.message);
    }
}

async function academyDetailsClicked() {
    const contentArea = document.querySelector('.content-area');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = ''; // Wyczyść zawartość przed załadowaniem nowych danych

    const header = document.createElement('h3');
    header.textContent = 'Academy Details';
    header.className = 'form-header';
    contentArea.appendChild(header);

    try {
        const response = await fetch('/api/admin/academy/info', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        const academyData = await response.json();

        const detailsContainer = document.createElement('div');
        detailsContainer.className = 'details-container';

        const fields = [
            { key: 'id', label: 'ID', value: academyData.id },
            { key: 'name', label: 'Name', value: academyData.name },
            { key: 'phone', label: 'Phone', value: academyData.phone },
            { key: 'email', label: 'Email', value: academyData.email },
            { key: 'addressId', label: 'Address ID', value: academyData.addressId },
            { key: 'deanId', label: 'Dean ID', value: academyData.deanId }
        ];

        const inputs = {}; // Obiekt do przechowywania pól wejściowych

        fields.forEach(field => {
            const detailRow = document.createElement('div');
            detailRow.className = 'detail-row';

            const label = document.createElement('span');
            label.textContent = `${field.label}: `;
            label.className = 'detail-label';

            const value = document.createElement('span');
            value.textContent = field.value || 'Not available';
            value.className = 'detail-value';

            const input = document.createElement('input');
            input.type = 'text';
            input.value = field.value || '';
            input.className = 'detail-input';
            input.style.display = 'none'; // Domyślnie ukryte
            inputs[field.key] = input;

            detailRow.appendChild(label);
            detailRow.appendChild(value);
            detailRow.appendChild(input);
            detailsContainer.appendChild(detailRow);
        });

        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.className = 'action-button edit-button';

        const cancelButton = document.createElement('button');
        cancelButton.textContent = 'Cancel';
        cancelButton.className = 'action-button cancel-button';
        cancelButton.style.display = 'none'; // Domyślnie ukryty

        const confirmButton = document.createElement('button');
        confirmButton.textContent = 'Confirm';
        confirmButton.className = 'action-button confirm-button';
        confirmButton.style.display = 'none'; // Domyślnie ukryty

        // Obsługa przycisku Edit
        editButton.addEventListener('click', () => {
            editButton.style.display = 'none';
            cancelButton.style.display = 'inline-block';
            confirmButton.style.display = 'inline-block';

            Object.keys(inputs).forEach(key => {
                inputs[key].style.display = 'block';
                const valueSpan = inputs[key].previousSibling;
                valueSpan.style.display = 'none';
            });
        });

        // Obsługa przycisku Cancel
        cancelButton.addEventListener('click', () => {
            editButton.style.display = 'inline-block';
            cancelButton.style.display = 'none';
            confirmButton.style.display = 'none';

            Object.keys(inputs).forEach(key => {
                inputs[key].style.display = 'none';
                const valueSpan = inputs[key].previousSibling;
                valueSpan.style.display = 'block';
            });
        });

        // Obsługa przycisku Confirm
        confirmButton.addEventListener('click', () => {
            const updatedData = {};
            let dataChanged = false;

            fields.forEach(field => {
                const inputValue = inputs[field.key].value;
                if (inputValue !== field.value) {
                    updatedData[field.key] = inputValue;
                    dataChanged = true;
                }
            });

            if (dataChanged) {
                console.log('Updated data:', updatedData);
                updateAcademyData(updatedData); // Funkcja do wysyłania danych na serwer
            } else {
                alert('No changes detected.');
            }

            // Przywracanie widoku po kliknięciu Confirm
            cancelButton.click();
        });

        contentArea.appendChild(detailsContainer);
        contentArea.appendChild(editButton);
        contentArea.appendChild(cancelButton);
        contentArea.appendChild(confirmButton);
    } catch (error) {
        console.error('Error fetching academy details:', error.message);
        contentArea.innerHTML = `<p style="color: red; text-align: center;">Failed to load academy details: ${error.message}</p>`;
    }
}

function addCourseClicked() {
    const contentArea = document.querySelector('.content-area');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '';

    const header = document.createElement('h3');
    header.textContent = 'Add New Course';
    header.className = 'form-header';
    contentArea.appendChild(header);

    const form = document.createElement('form');
    form.className = 'add-course-form';

    // Course Name field
    const nameField = createInputField('Course Name', 'text', 'course-name');

    // Course Description field (large textarea)
    const descriptionField = document.createElement('div');
    descriptionField.className = 'form-field';
    const descriptionLabel = document.createElement('label');
    descriptionLabel.textContent = 'Description';
    descriptionLabel.htmlFor = 'course-description';
    const descriptionTextarea = document.createElement('textarea');
    descriptionTextarea.id = 'course-description';
    descriptionTextarea.className = 'form-input';
    descriptionTextarea.rows = 5;
    descriptionTextarea.placeholder = 'Enter detailed course description here';
    descriptionField.appendChild(descriptionLabel);
    descriptionField.appendChild(descriptionTextarea);

    // ECTS Credits field with +/- buttons
    const ectsCreditsField = document.createElement('div');
    ectsCreditsField.className = 'form-field';

    const ectsLabel = document.createElement('label');
    ectsLabel.textContent = 'ECTS Credits';
    ectsLabel.htmlFor = 'ects-credits';

    const decrementButton = document.createElement('button');
    decrementButton.type = 'button';
    decrementButton.textContent = '-';
    decrementButton.className = 'adjust-button';

    const ectsInput = document.createElement('input');
    ectsInput.type = 'number';
    ectsInput.id = 'ects-credits';
    ectsInput.className = 'form-input';
    ectsInput.min = 1;
    ectsInput.max = 99;
    ectsInput.value = 1;

    const incrementButton = document.createElement('button');
    incrementButton.type = 'button';
    incrementButton.textContent = '+';
    incrementButton.className = 'adjust-button';

    decrementButton.addEventListener('click', () => {
        if (ectsInput.value > 1) {
            ectsInput.value = parseInt(ectsInput.value) - 1;
        }
    });

    incrementButton.addEventListener('click', () => {
        if (ectsInput.value < 99) {
            ectsInput.value = parseInt(ectsInput.value) + 1;
        }
    });

    ectsCreditsField.appendChild(ectsLabel);
    ectsCreditsField.appendChild(decrementButton);
    ectsCreditsField.appendChild(ectsInput);
    ectsCreditsField.appendChild(incrementButton);

    // Select Lecturer functionality
    const lecturerField = document.createElement('div');
    lecturerField.className = 'form-field';
    const lecturerLabel = document.createElement('label');
    lecturerLabel.textContent = 'Select Lecturer(s)';

    const selectedLecturersList = document.createElement('ul');
    selectedLecturersList.className = 'selected-lecturers-list';

    const selectLecturerButton = document.createElement('button');
    selectLecturerButton.type = 'button';
    selectLecturerButton.textContent = 'Select Lecturer';
    selectLecturerButton.className = 'select-lecturer-button';

    selectLecturerButton.addEventListener('click', () => {
        selectLecturer();
    });

    lecturerField.appendChild(lecturerLabel);
    lecturerField.appendChild(selectedLecturersList);
    lecturerField.appendChild(selectLecturerButton);

    const submitButton = document.createElement('button');
    submitButton.type = 'button';
    submitButton.textContent = 'Add Course';
    submitButton.className = 'submit-button';

    submitButton.addEventListener('click', () => {
        const selectedLecturers = Array.from(selectedLecturersList.children)
            .map(li => li.textContent.replace('Remove', '').trim())[0]; // Get only the first lecturer

        const newCourseRequest = {
            name: nameField.querySelector('input').value,
            description: descriptionTextarea.value,
            ectsCredits: Number(ectsInput.value),
            lecturers: selectedLecturers,
        };

        if (!newCourseRequest.name || !newCourseRequest.description || !newCourseRequest.ectsCredits || !newCourseRequest.lecturers) {
            alert('Please fill in all required fields and select at least one lecturer.');
            return;
        }

        console.log('New course request:', newCourseRequest);
        addNewCourse(newCourseRequest);
    });


    form.appendChild(nameField);
    form.appendChild(descriptionField);
    form.appendChild(ectsCreditsField);
    form.appendChild(lecturerField);
    form.appendChild(submitButton);

    contentArea.appendChild(form);
}

async function selectLecturer() {
    const modal = document.createElement('div');
    modal.className = 'modal';

    const modalContent = document.createElement('div');
    modalContent.className = 'modal-content';
    modalContent.innerHTML = '<h3 class="modal-header">Select Lecturer</h3><p class="loading-message">Loading lecturers...</p>';

    modal.appendChild(modalContent);
    document.body.appendChild(modal);

    try {
        const response = await fetch('/api/admin/lecturers', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch lecturers. Status: ${response.status}`);
        }

        const lecturers = await response.json();

        modalContent.innerHTML = ''; // Clear loading message

        if (lecturers.length === 0) {
            modalContent.innerHTML = '<p class="no-lecturers-message">No lecturers found.</p>';
            return;
        }

        const lecturersTable = document.createElement('table');
        lecturersTable.className = 'lecturers-table';

        // Table header
        const headerRow = document.createElement('tr');
        headerRow.innerHTML = `
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Academic Title</th>
            <th>Specialization</th>
            <th>Actions</th>
        `;
        lecturersTable.appendChild(headerRow);

        // Populate table rows
        lecturers.forEach(lecturer => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${lecturer.id}</td>
                <td>${lecturer.firstName}</td>
                <td>${lecturer.lastName}</td>
                <td>${lecturer.academicTitle}</td>
                <td>${lecturer.specialization}</td>
                <td>
                    <button class="action-btn add-lecturer-btn" data-lecturer-id="${lecturer.id}" data-lecturer-name="${lecturer.firstName} ${lecturer.lastName}">Add</button>
                </td>
            `;
            lecturersTable.appendChild(row);
        });

        modalContent.appendChild(lecturersTable);

        // Add event listeners for add buttons
        const addLecturerButtons = document.querySelectorAll('.add-lecturer-btn');

        addLecturerButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                const lecturerId = event.target.getAttribute('data-lecturer-id');
                const lecturerName = event.target.getAttribute('data-lecturer-name');

                const selectedLecturersList = document.querySelector('.selected-lecturers-list');
                if (selectedLecturersList) {
                    const lecturerItem = document.createElement('li');
                    lecturerItem.textContent = lecturerName;

                    const removeButton = document.createElement('button');
                    removeButton.textContent = 'Remove';
                    removeButton.className = 'remove-button';
                    removeButton.addEventListener('click', () => {
                        selectedLecturersList.removeChild(lecturerItem);
                    });

                    lecturerItem.appendChild(removeButton);
                    selectedLecturersList.appendChild(lecturerItem);
                }

                // Close the modal after adding
                document.body.removeChild(modal);
            });
        });

    } catch (error) {
        console.error('Error fetching lecturers:', error);
        modalContent.innerHTML = '<p class="error-message">Failed to load lecturers. Please try again later.</p>';
    }

    // Add close button to modal
    const closeButton = document.createElement('button');
    closeButton.textContent = 'Close';
    closeButton.className = 'modal-close-btn';
    closeButton.addEventListener('click', () => {
        document.body.removeChild(modal);
    });
    modalContent.appendChild(closeButton);
}

function createInputField(labelText, inputType, inputId) {
    const fieldContainer = document.createElement('div');
    fieldContainer.className = 'form-field';

    const label = document.createElement('label');
    label.textContent = labelText;
    label.htmlFor = inputId;

    const input = document.createElement('input');
    input.type = inputType;
    input.id = inputId;
    input.className = 'form-input';

    fieldContainer.appendChild(label);
    fieldContainer.appendChild(input);

    return fieldContainer;
}

function addNewCourse(courseData) {
    console.log('Sending course data to the backend:', courseData);

    fetch('/api/admin/courses/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(courseData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to add course. Status: ${response.status}`);
            }
            return response.text(); // Assuming the Java method returns plain text
        })
        .then(data => {
            console.log('Course added successfully:', data);
            alert('Course added successfully!');
        })
        .catch(error => {
            console.error('Error adding course:', error);
            alert('Failed to add course. Please try again.');
        });
}


function showCoursesClicked() {
    const contentArea = document.querySelector('.content-area');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '<h3 class="loading-message">Loading courses...</h3>';

    fetch('/api/admin/courses', {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch courses. Status: ${response.status}`);
            }
            return response.json();
        })
        .then(courses => {
            contentArea.innerHTML = ''; // Clear loading message

            if (courses.length === 0) {
                contentArea.innerHTML = '<p class="no-courses-message">No courses found.</p>';
                return;
            }

            const coursesTable = document.createElement('table');
            coursesTable.className = 'courses-table';

            // Table header
            const headerRow = document.createElement('tr');
            headerRow.innerHTML = `
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>ECTS Credits</th>
            <th>Lecturer</th>
        `;
            coursesTable.appendChild(headerRow);

            courses.forEach(course => {
                const row = document.createElement('tr');
                row.innerHTML = `
                <td>${course.id}</td>
                <td>${course.name}</td>
                <td>${course.description}</td>
                <td>${course.ectsCredits}</td>
                <td>${course.lecturer.firstName} ${course.lecturer.lastName}</td>
            `;
                coursesTable.appendChild(row);
            });

            contentArea.appendChild(coursesTable);
        })
        .catch(error => {
            console.error('Error fetching courses:', error);
            contentArea.innerHTML = '<p class="error-message">Failed to load courses. Please try again later.</p>';
        });
}



