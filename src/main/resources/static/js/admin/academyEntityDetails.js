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

        // Sekcja General Information
        const generalSection = createSection('General Information', [
            { key: 'name', label: 'Name', value: academyData.name },
            { key: 'phone', label: 'Phone', value: academyData.phone },
            { key: 'email', label: 'Email', value: academyData.email },
            { key: 'street', label: 'Street', value: academyData.address.street },
            { key: 'houseNumber', label: 'House Number', value: academyData.address.houseNumber },
            { key: 'apartmentNumber', label: 'Apartment Number', value: academyData.address.apartmentNumber },
            { key: 'city', label: 'City', value: academyData.address.city },
            { key: 'postalCode', label: 'Postal Code', value: academyData.address.postalCode },
            { key: 'country', label: 'Country', value: academyData.address.country }
        ], true, async (updatedData) => {
            const success = await updateAcademyData(updatedData);
            if (success) {
                return true
            } else {
                alert('Failed to update academy details.');
            }
        });

        // Sekcja Dean Details
        const deanSection = createSection('Dean Details', [
            { key: 'firstName', label: 'First Name', value: academyData.dean.firstName },
            { key: 'lastName', label: 'Last Name', value: academyData.dean.lastName },
            { key: 'email', label: 'Email', value: academyData.dean.email },
            { key: 'phone', label: 'Phone', value: academyData.dean.phoneNumber }
        ], false);

        const changeDeanButton = document.createElement('button');
        changeDeanButton.textContent = 'Change Dean';
        changeDeanButton.className = 'sd-action-button sd-change-dean-button';
        changeDeanButton.addEventListener('click', () => {
            changeDean(); // Funkcja do zaimplementowania
        });
        deanSection.appendChild(changeDeanButton);

        detailsContainer.appendChild(generalSection);
        detailsContainer.appendChild(deanSection);
        contentArea.appendChild(detailsContainer);

    } catch (error) {
        console.error('Error fetching academy details:', error.message);
        contentArea.innerHTML = `<p class="sd-error-message">Failed to load academy details: ${error.message}</p>`;
        return false;
    }
}

function createSection(title, fields, editable = false, onSave = null) {
    const section = document.createElement('div');
    section.className = 'sd-section';

    const sectionHeader = document.createElement('h4');
    sectionHeader.textContent = title;
    sectionHeader.className = 'sd-section-header';
    section.appendChild(sectionHeader);

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
        section.appendChild(detailRow);
    });

    if (editable) {
        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.className = 'sd-action-button sd-edit-button';

        const cancelButton = document.createElement('button');
        cancelButton.textContent = 'Cancel';
        cancelButton.className = 'sd-action-button sd-cancel-button';
        cancelButton.style.display = 'none';

        const saveButton = document.createElement('button');
        saveButton.textContent = 'Save';
        saveButton.className = 'sd-action-button sd-save-button';
        saveButton.style.display = 'none';

        editButton.addEventListener('click', () => {
            editButton.style.display = 'none';
            cancelButton.style.display = 'inline-block';
            saveButton.style.display = 'inline-block';

            Object.keys(inputs).forEach(key => {
                inputs[key].style.display = 'block';
                const valueSpan = inputs[key].previousSibling;
                valueSpan.style.display = 'none';
            });
        });

        cancelButton.addEventListener('click', () => {
            editButton.style.display = 'inline-block';
            cancelButton.style.display = 'none';
            saveButton.style.display = 'none';

            Object.keys(inputs).forEach(key => {
                inputs[key].style.display = 'none';
                const valueSpan = inputs[key].previousSibling;
                valueSpan.style.display = 'block';
            });
        });

        saveButton.addEventListener('click', () => {
            const updatedData = {};
            let dataChanged = false;

            fields.forEach(field => {
                const inputValue = inputs[field.key].value;
                if (inputValue !== field.value) {
                    updatedData[field.key] = inputValue;
                    dataChanged = true;
                }
            });

            if (dataChanged && onSave) {
                onSave(updatedData);
            } else {
                alert('No changes detected.');
            }

            cancelButton.click();
        });

        section.appendChild(editButton);
        section.appendChild(cancelButton);
        section.appendChild(saveButton);
    }

    return section;
}
async function updateAcademyData(updatedData) {
    try {
        // Wysłanie żądania POST z zaktualizowanymi danymi akademii
        const response = await fetch('/api/admin/academy/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData) // Konwertowanie danych do JSON
        });

        // Sprawdzenie statusu odpowiedzi
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Failed to update academy data:', errorText);
            alert(`Error: ${errorText}`);
            return false; // Zwracamy `false` w przypadku błędu
        }

        // Pobranie komunikatu o sukcesie
        const successMessage = await response.text();
        console.log('Academy data updated successfully:', successMessage);
        return true; // Zwracamy `true` w przypadku sukcesu
    } catch (error) {
        // Obsługa błędów podczas wysyłania żądania
        console.error('Error updating academy data:', error);
        alert('An error occurred while updating academy data.');
        return false; // Zwracamy `false` w przypadku błędu
    }
}


