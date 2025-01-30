

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



