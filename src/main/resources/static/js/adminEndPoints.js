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
        console.log('User added successfully:', successMessage);
        alert('User added successfully!');
    } catch (error) {
        console.error('Error adding user:', error);
        alert('An error occurred while adding the user.');
    }
}
