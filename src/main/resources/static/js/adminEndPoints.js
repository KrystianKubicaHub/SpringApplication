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
        handleOptionSelection(buttonb1, )
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

