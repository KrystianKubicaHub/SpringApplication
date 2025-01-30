async function showCoursesClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '<h3 class="showCourse-loading-message">Loading courses...</h3>';

    try {
        const response = await fetch('/api/admin/courses', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch courses. Status: ${response.status}`);
        }

        const courses = await response.json();

        contentArea.innerHTML = '';

        const addButton = document.createElement('button');
        addButton.className = 'showCourse-add-btn';
        addButton.textContent = 'Add Course';
        addButton.addEventListener('click', () => {
            console.log('Add Course clicked');
            addNewCourseClicked();
        });

        contentArea.appendChild(addButton);

        if (courses.length === 0) {
            contentArea.innerHTML += '<p class="showCourse-no-courses-message">No courses found.</p>';
            return;
        }

        const coursesTable = document.createElement('table');
        coursesTable.className = 'showCourse-table';

        const headerRow = document.createElement('tr');
        headerRow.innerHTML = `
            <th>Name</th>
            <th>Description</th>
            <th>ECTS</th>
            <th>Lecturer</th>
            <th>Actions</th>
        `;
        coursesTable.appendChild(headerRow);

        courses.forEach(course => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${course.name}</td>
                <td>${course.description}</td>
                <td>${course.ectsCredits}</td>
                <td>${course.lecturer ? course.lecturer.firstName + ' ' + course.lecturer.lastName : 'N/A'}</td>
                <td>
                    <button class="showCourse-delete-btn" data-course-id="${course.id}">Delete</button>
                </td>
            `;
            coursesTable.appendChild(row);
        });

        contentArea.appendChild(coursesTable);

        const deleteButtons = document.querySelectorAll('.showCourse-delete-btn');
        deleteButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                const courseId = event.target.getAttribute('data-course-id');
                console.log(`Delete clicked for course ID: ${courseId}`);
                deleteCourse(courseId);
            });
        });

    } catch (error) {
        console.error('Error fetching courses:', error);
        contentArea.innerHTML = '<p class="showCourse-error-message">Failed to load courses. Please try again later.</p>';
    }
}

async function deleteCourse(courseId) {
    try {
        const response = await fetch(`/api/admin/courses/${courseId}`, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error(`Failed to delete course. Status: ${response.status}`);
        }

        showCoursesClicked();
    } catch (error) {
        console.error(`Error deleting course:`, error);
        alert(`Failed to delete course. Please try again.`);
    }
}


function addNewCourseClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '';

    const header = document.createElement('h3');
    header.textContent = 'Add New Course';
    header.className = 'addNewCourse-header';
    contentArea.appendChild(header);

    const form = document.createElement('form');
    form.className = 'addNewCourse-form';

    const nameField = createInputField('Course Name', 'text', 'addNewCourse-name');

    const descriptionField = document.createElement('div');
    descriptionField.className = 'addNewCourse-field';
    const descriptionLabel = document.createElement('label');
    descriptionLabel.textContent = 'Description';
    descriptionLabel.htmlFor = 'addNewCourse-description';
    const descriptionTextarea = document.createElement('textarea');
    descriptionTextarea.id = 'addNewCourse-description';
    descriptionTextarea.className = 'addNewCourse-input';
    descriptionTextarea.rows = 5;
    descriptionTextarea.placeholder = 'Enter detailed course description here';
    descriptionField.appendChild(descriptionLabel);
    descriptionField.appendChild(descriptionTextarea);

    const ectsCreditsField = document.createElement('div');
    ectsCreditsField.className = 'addNewCourse-field';

    const ectsLabel = document.createElement('label');
    ectsLabel.textContent = 'ECTS Credits';
    ectsLabel.htmlFor = 'addNewCourse-ects';

    const decrementButton = document.createElement('button');
    decrementButton.type = 'button';
    decrementButton.textContent = '-';
    decrementButton.className = 'addNewCourse-adjust-btn';

    const ectsInput = document.createElement('input');
    ectsInput.type = 'number';
    ectsInput.id = 'addNewCourse-ects';
    ectsInput.className = 'addNewCourse-input';
    ectsInput.min = 1;
    ectsInput.max = 99;
    ectsInput.value = 1;

    const incrementButton = document.createElement('button');
    incrementButton.type = 'button';
    incrementButton.textContent = '+';
    incrementButton.className = 'addNewCourse-adjust-btn';

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

    // Pole do wyboru wykładowcy
    let selectedLecturerId = null;

    const lecturerField = document.createElement('div');
    lecturerField.className = 'addNewCourse-field';

    const lecturerLabel = document.createElement('label');
    lecturerLabel.textContent = 'Select Lecturer:';

    // Przycisk do wybrania wykładowcy
    const selectLecturerButton = document.createElement('button');
    selectLecturerButton.type = 'button';
    selectLecturerButton.textContent = 'Choose Lecturer';
    selectLecturerButton.className = 'addNewCourse-select-btn';

    // Dynamicznie generowane `div` dla wyboru i wyniku
    const selectLecturerContainer = document.createElement('div');
    selectLecturerContainer.className = 'addNewCourse-select-lecturer-container';

    const selectedLecturerResult = document.createElement('div');
    selectedLecturerResult.className = 'addNewCourse-selected-lecturer';

    selectLecturerButton.addEventListener('click', () => {
        console.log('Opening lecturer selection...');

        selectLecturer(selectLecturerContainer, selectedLecturerResult, (lecturerId, lecturerName) => {
            selectedLecturerId = lecturerId;
            selectedLecturerResult.innerHTML = `
                <p class="addNewCourse-selected">Selected Lecturer: ${lecturerName}</p>
            `;

            // Obsługa ponownego wyboru
            document.querySelector('.addNewCourse-select-again-btn').addEventListener('click', () => {
                selectLecturerContainer.style.display = 'block';
            });

            selectLecturerContainer.style.display = 'none';
            console.log(`Selected Lecturer ID: ${selectedLecturerId}, Name: ${lecturerName}`);
        });

        selectLecturerContainer.style.display = 'block';
    });

    lecturerField.appendChild(lecturerLabel);
    lecturerField.appendChild(selectLecturerButton);
    lecturerField.appendChild(selectedLecturerResult);
    lecturerField.appendChild(selectLecturerContainer);

    const submitButton = document.createElement('button');
    submitButton.type = 'button';
    submitButton.textContent = 'Add Course';
    submitButton.className = 'addNewCourse-submit-btn';

    submitButton.addEventListener('click', () => {
        const newCourseRequest = {
            name: nameField.querySelector('input').value,
            description: descriptionTextarea.value,
            ectsCredits: Number(ectsInput.value),
            lecturerId: selectedLecturerId,
        };

        if (!newCourseRequest.name || !newCourseRequest.description || !newCourseRequest.ectsCredits || !newCourseRequest.lecturerId) {
            alert('Please fill in all required fields and select a lecturer.');
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

async function selectLecturer(targetElement, resultElement, callback) {
    if (!targetElement || !resultElement) {
        console.error('Target or result element not found');
        return;
    }

    // Pokazanie panelu wyboru wykładowcy
    targetElement.style.display = 'block';
    targetElement.innerHTML = '<p class="addNewCourse-loading-message">Loading lecturers...</p>';
    resultElement.innerHTML = ''; // Czyścimy wynik

    try {
        const response = await fetch('/api/admin/lecturers', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch lecturers. Status: ${response.status}`);
        }

        const lecturers = await response.json();

        // Czyszczenie i dodanie nagłówka
        targetElement.innerHTML = '';
        const header = document.createElement('h3');
        header.textContent = 'Select Lecturer';
        header.className = 'addNewCourse-lecturer-header';
        targetElement.appendChild(header);

        if (lecturers.length === 0) {
            targetElement.innerHTML += '<p class="addNewCourse-no-lecturers-message">No lecturers found.</p>';
            return;
        }

        // Tworzenie listy wykładowców
        const lecturersList = document.createElement('ul');
        lecturersList.className = 'addNewCourse-lecturers-list';

        lecturers.forEach(lecturer => {
            const listItem = document.createElement('li');
            listItem.className = 'addNewCourse-lecturer-item';
            listItem.textContent = `${lecturer.academicTitle} ${lecturer.firstName} ${lecturer.lastName}`;
            listItem.dataset.lecturerId = lecturer.id;

            listItem.addEventListener('click', () => {
                const selectedLecturer = listItem.textContent;
                const selectedLecturerId = listItem.dataset.lecturerId;

                resultElement.innerHTML = `<p class="addNewCourse-selected">Selected Lecturer: ${selectedLecturer}</p>`;

                targetElement.style.display = 'none';

                callback(selectedLecturerId, selectedLecturer);
            });

            lecturersList.appendChild(listItem);
        });

        targetElement.appendChild(lecturersList);
    } catch (error) {
        console.error('Error fetching lecturers:', error);
        targetElement.innerHTML = '<p class="addNewCourse-error-message">Failed to load lecturers. Please try again later.</p>';
    }
}



function addNewCourse(courseData) {
    console.log('Sending course data to the backend:', courseData);

    fetch('/api/admin/courses/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: courseData.name,
            description: courseData.description,
            ectsCredits: courseData.ectsCredits,
            lecturerId: courseData.lecturerId
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to add course. Status: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {
            console.log('Course added successfully:', data);
            showCoursesClicked();
        })
        .catch(error => {
            console.error('Error adding course:', error);
            alert('Failed to add course. Please try again.');
        });
}



