async function showFieldsOfStudyClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '<h3 class="showField-loading-message">Loading fields of study...</h3>';

    try {
        const response = await fetch('/api/admin/fieldsOfStudy', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch fields of study. Status: ${response.status}`);
        }

        const fieldsOfStudy = await response.json();

        contentArea.innerHTML = '';

        const addButton = document.createElement('button');
        addButton.className = 'showField-add-btn';
        addButton.textContent = 'Add Field of Study';
        addButton.addEventListener('click', () => {
            addFieldOfStudy();
        });

        contentArea.appendChild(addButton);

        if (fieldsOfStudy.length === 0) {
            contentArea.innerHTML += '<p class="showField-no-fields-message">No fields of study found.</p>';
            return;
        }

        const fieldsTable = document.createElement('table');
        fieldsTable.className = 'showField-table';

        const headerRow = document.createElement('tr');
        headerRow.innerHTML = `
            <th>Field Name</th>
            <th>Study Level</th>
            <th>Duration (Semesters)</th>
            <th>Description</th>
            <th>Actions</th>
        `;
        fieldsTable.appendChild(headerRow);

        fieldsOfStudy.forEach(field => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${field.fieldName}</td>
                <td>${field.studyLevel}</td>
                <td>${field.durationInSemesters}</td>
                <td>${field.description}</td>
                <td>
                    <button class="showField-delete-btn" data-field-id="${field.idField}">Delete</button>
                </td>
            `;
            fieldsTable.appendChild(row);
        });

        contentArea.appendChild(fieldsTable);

        const deleteButtons = document.querySelectorAll('.showField-delete-btn');
        deleteButtons.forEach(button => {
            button.addEventListener('click', (event) => {
                const fieldId = event.target.getAttribute('data-field-id');
                console.log(`Delete clicked for field ID: ${fieldId}`);
                deleteFieldOfStudy(fieldId);
            });
        });

    } catch (error) {
        console.error('Error fetching fields of study:', error);
        contentArea.innerHTML = '<p class="showField-error-message">Failed to load fields of study. Please try again later.</p>';
    }
}

async function deleteFieldOfStudy(id) {
    //const confirmDelete = confirm(`Are you sure you want to delete field of study with ID: ${id}?`);
    //if (!confirmDelete) return;
    try {
        const response = await fetch(`/api/admin/fieldsOfStudy/${id}`, { method: 'DELETE' });


        if (!response.ok) {
            throw new Error(`Failed to delete field of study. Status: ${response.status}`);
        }

        console.log(`Field of study with ID: ${id} deleted successfully.`);

        showFieldsOfStudyClicked();
    } catch (error) {
        console.error(`Error deleting field of study:`, error);
        alert(`Failed to delete field of study. Please try again.`);
    }
}

function addFieldOfStudy() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = `
        <h3 class="addField-form-title">Add New Field of Study</h3>
        <form id="addField-form">
            <label for="addField-name">Field Name:</label>
            <input type="text" id="addField-name" required>

            <label for="addField-level">Study Level:</label>
            <select id="addField-level" required>
                <option value="BACHELOR">Bachelor</option>
                <option value="MASTER">Master</option>
                <option value="ENGINEER">Engineer</option>
                <option value="DOCTORATE">Doctorate</option>
            </select>

            <label for="addField-duration">Duration (Semesters):</label>
            <input type="number" id="addField-duration" min="1" max="12" required>

            <label for="addField-description">Description:</label>
            <textarea id="addField-description" rows="3"></textarea>

            <div class="addField-buttons">
                <button type="button" class="addField-back-btn">Back</button>
                <button type="submit" class="addField-add-btn">Add</button>
            </div>
        </form>
    `;

    document.querySelector('.addField-back-btn').addEventListener('click', showFieldsOfStudyClicked);

    document.getElementById('addField-form').addEventListener('submit', async (event) => {
        event.preventDefault();

        const newField = {
            fieldName: document.getElementById('addField-name').value,
            studyLevel: document.getElementById('addField-level').value,
            durationInSemesters: parseInt(document.getElementById('addField-duration').value, 10),
            description: document.getElementById('addField-description').value
        };

        try {
            const response = await fetch('/api/admin/fieldsOfStudy/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newField)
            });

            if (!response.ok) {
                throw new Error(`Failed to add field of study. Status: ${response.status}`);
            }
            showFieldsOfStudyClicked();
        } catch (error) {
            console.error('Error adding field of study:', error);
            alert('Failed to add field of study. Please try again.');
        }
    });
}



