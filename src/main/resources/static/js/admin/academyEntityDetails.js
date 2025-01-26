async function academyDetailsClicked() {
    const contentArea = document.getElementById('content-panel');
    if (!contentArea) {
        console.error('Content area not found');
        return false;
    }

    contentArea.innerHTML = '';

    const header = document.createElement('h3');
    header.textContent = 'Academy Details';
    header.className = 'sd-form-header';
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
        detailsContainer.className = 'sd-details-container';

        const fields = [
            { key: 'id', label: 'ID', value: academyData.id },
            { key: 'name', label: 'Name', value: academyData.name },
            { key: 'phone', label: 'Phone', value: academyData.phone },
            { key: 'email', label: 'Email', value: academyData.email },
            { key: 'addressId', label: 'Address ID', value: academyData.addressId },
            { key: 'deanId', label: 'Dean ID', value: academyData.deanId }
        ];

        const inputs = {};

        fields.forEach(field => {
            const detailRow = document.createElement('div');
            detailRow.className = 'sd-detail-row';

            const label = document.createElement('span');
            label.textContent = `${field.label}: `;
            label.className = 'sd-detail-label';

            const value = document.createElement('span');
            value.textContent = field.value || 'Not available';
            value.className = 'sd-detail-value';

            const input = document.createElement('input');
            input.type = 'text';
            input.value = field.value || '';
            input.className = 'sd-detail-input';
            input.style.display = 'none';
            inputs[field.key] = input;

            detailRow.appendChild(label);
            detailRow.appendChild(value);
            detailRow.appendChild(input);
            detailsContainer.appendChild(detailRow);
        });

        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.className = 'sd-action-button sd-edit-button';

        const cancelButton = document.createElement('button');
        cancelButton.textContent = 'Cancel';
        cancelButton.className = 'sd-action-button sd-cancel-button';
        cancelButton.style.display = 'none'; // Domyślnie ukryty

        const confirmButton = document.createElement('button');
        confirmButton.textContent = 'Confirm';
        confirmButton.className = 'sd-action-button sd-confirm-button';
        confirmButton.style.display = 'none'; // Domyślnie ukryty

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

        confirmButton.addEventListener('click', async () => {
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
                const success = await updateAcademyData(updatedData);
                if (success) {
                    cancelButton.click();
                    return true;
                } else {
                    alert('Failed to update academy data.');
                    return false;
                }
            } else {
                alert('No changes detected.');
                return false;
            }
        });

        contentArea.appendChild(detailsContainer);
        contentArea.appendChild(editButton);
        contentArea.appendChild(cancelButton);
        contentArea.appendChild(confirmButton);
    } catch (error) {
        console.error('Error fetching academy details:', error.message);
        contentArea.innerHTML = `<p class="sd-error-message">Failed to load academy details: ${error.message}</p>`;
        return false;
    }
}
