async function showPeopleClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return;
    }

    contentArea.innerHTML = '<h3 class="shp-loading-message">Loading users...</h3>';

    try {
        const response = await fetch('/api/admin/users', { method: 'GET' });
        if (!response.ok) {
            throw new Error(`Failed to fetch users. Status: ${response.status}`);
        }

        const users = await response.json();

        contentArea.innerHTML = '';

        if (users.length === 0) {
            contentArea.innerHTML = '<p class="shp-no-users-message">No users found.</p>';
            return;
        }

        const usersTable = document.createElement('table');
        usersTable.className = 'shp-users-table';

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
                    <button class="shp-action-btn shp-show-more-btn" data-user-id="${user.id}">Show More</button>
                    <button class="shp-action-btn shp-edit-btn" data-user-id="${user.id}">Edit</button>
                    <button class="shp-action-btn shp-delete-btn" data-user-id="${user.id}">Delete</button>
                </td>
            `;
            usersTable.appendChild(row);
        });

        contentArea.appendChild(usersTable);

        const showMoreButtons = document.querySelectorAll('.shp-show-more-btn');
        const editButtons = document.querySelectorAll('.shp-edit-btn');
        const deleteButtons = document.querySelectorAll('.shp-delete-btn');

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
            });
        });

        deleteButtons.forEach(button => {
            button.addEventListener('click', async (event) => {
                const userId = event.target.getAttribute('data-user-id');
                console.log(`Delete clicked for user ID: ${userId}`);
                const confirmDelete = confirm(`Are you sure you want to delete user ID: ${userId}?`);
                if (confirmDelete) {
                    await deleteUser(userId);
                    showPeopleClicked();
                }
            });
        });

    } catch (error) {
        console.error('Error fetching users:', error);
        contentArea.innerHTML = '<p class="shp-error-message">Failed to load users. Please try again later.</p>';
    }
}
