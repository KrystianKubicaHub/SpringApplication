// Przyciski
const toggleInfoBtn = document.getElementById('toggle-info-btn');
const editBtn = document.getElementById('edit-btn');
const cancelBtn = document.getElementById('cancel-btn');
const additionalInfo = document.getElementById('additional-info');


// Sekcje informacji
const adminNote = document.getElementById('admin-note');

// Pola edytowalne
const firstNameSpan = document.getElementById('first-name');
const lastNameSpan = document.getElementById('last-name');
const emailSpan = document.getElementById('email');
const phoneNumberSpan = document.getElementById('phone-number');
const firstNameInput = document.getElementById('first-name-input');
const lastNameInput = document.getElementById('last-name-input');
const emailInput = document.getElementById('email-input');
const phoneNumberInput = document.getElementById('phone-number-input');

// Zmienna przechowująca pierwotne wartości
let originalData = {};

// Funkcja: Przełączanie widoczności szczegółów
toggleInfoBtn.addEventListener("click", function () {
    if (additionalInfo.style.display === "none" || !additionalInfo.style.display) {
        additionalInfo.style.display = "block";
        toggleInfoBtn.textContent = "Show Less";
    } else {
        additionalInfo.style.display = "none";
        toggleInfoBtn.textContent = "Show All";
    }
});


// Funkcja: Rozpoczęcie edycji
function startEditMode() {
    editBtn.textContent = "Confirm";
    cancelBtn.style.display = "inline-block";

    // Ukryj przycisk Show All / Show Less
    toggleInfoBtn.style.display = "none";

    // Pokaż notatkę admina
    adminNote.style.display = "block";

    // Zapamiętaj pierwotne wartości
    originalData = {
        firstName: firstNameSpan.textContent,
        lastName: lastNameSpan.textContent,
        email: emailSpan.textContent,
        phoneNumber: phoneNumberSpan.textContent
    };

    // Zamień dane na pola edycji
    firstNameInput.value = firstNameSpan.textContent;
    lastNameInput.value = lastNameSpan.textContent;
    emailInput.value = emailSpan.textContent;
    phoneNumberInput.value = phoneNumberSpan.textContent;

    firstNameSpan.style.display = "none";
    lastNameSpan.style.display = "none";
    emailSpan.style.display = "none";
    phoneNumberSpan.style.display = "none";

    firstNameInput.style.display = "inline-block";
    lastNameInput.style.display = "inline-block";
    emailInput.style.display = "inline-block";
    phoneNumberInput.style.display = "inline-block";
}

// Funkcja: Sprawdzenie, czy dokonano zmian
function hasChanges() {
    return (
        firstNameInput.value !== originalData.firstName ||
        lastNameInput.value !== originalData.lastName ||
        emailInput.value !== originalData.email ||
        phoneNumberInput.value !== originalData.phoneNumber
    );
}

// Funkcja: Zatwierdzenie zmian
function confirmChanges() {
    if (hasChanges()) {
        if (confirm("Are you sure you want to save these changes?")) {
            // Wywołaj odpowiednie funkcje backendu
            updateFirstName(firstNameInput.value);
            updateLastName(lastNameInput.value);
            updateEmail(emailInput.value);
            updatePhoneNumber(phoneNumberInput.value);

            alert("Changes saved successfully.");
        } else {
            // Przywróć pierwotne dane, jeśli użytkownik anulował potwierdzenie
            resetEditMode();
        }
    } else {
        alert("No changes detected.");
    }

    resetEditMode();
}

// Funkcja: Anulowanie edycji
function cancelChanges() {
    resetEditMode();
}

// Funkcja: Przywrócenie trybu widoku
function resetEditMode() {
    editBtn.textContent = "Edit";
    cancelBtn.style.display = "none";

    // Pokaż przycisk Show All / Show Less
    toggleInfoBtn.style.display = "inline-block";

    // Ukryj notatkę admina
    adminNote.style.display = "none";

    // Przywróć pierwotne dane
    firstNameSpan.textContent = originalData.firstName;
    lastNameSpan.textContent = originalData.lastName;
    emailSpan.textContent = originalData.email;
    phoneNumberSpan.textContent = originalData.phoneNumber;

    firstNameSpan.style.display = "inline";
    lastNameSpan.style.display = "inline";
    emailSpan.style.display = "inline";
    phoneNumberSpan.style.display = "inline";

    firstNameInput.style.display = "none";
    lastNameInput.style.display = "none";
    emailInput.style.display = "none";
    phoneNumberInput.style.display = "none";
}

// Obsługa przycisku Edit
editBtn.addEventListener("click", function () {
    if (editBtn.textContent === "Edit") {
        startEditMode(); // Przełączenie na tryb edycji
    } else {
        confirmChanges(); // Zatwierdzenie zmian
    }
});

cancelBtn.addEventListener("click", function () {
    cancelChanges();
});
function confirmResignation(courseId) {
    if (confirm("Are you sure you want to resign from this course?")) {
       fetch(`/api/resign/${courseId}`, {
            method: 'POST',
        })
        .then(response => {
            if (response.ok) {
                alert("You have successfully resigned from the course.");
                window.location.reload();
            } else {
                alert("There was an error processing your request.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("An error occurred.");
        });
    }
}


// Funkcje wywołujące backend (do zaimplementowania w backendzie)
function updateFirstName(newFirstName) {
    fetch('/api/updateFirstName', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ firstName: newFirstName })
    }).catch(error => console.error("Error updating first name:", error));
}

function updateLastName(newLastName) {
    fetch('/api/updateLastName', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ lastName: newLastName })
    }).catch(error => console.error("Error updating last name:", error));
}

function updateEmail(newEmail) {
    fetch('/api/updateEmail', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: newEmail })
    }).catch(error => console.error("Error updating email:", error));
}

function updatePhoneNumber(newPhoneNumber) {
    fetch('/api/updatePhoneNumber', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ phoneNumber: newPhoneNumber })
    }).catch(error => console.error("Error updating phone number:", error));
}