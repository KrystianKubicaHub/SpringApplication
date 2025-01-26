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



