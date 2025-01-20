document.addEventListener('DOMContentLoaded', () => {
    // Obsługa formularza logowania
    const loginForm = document.getElementById('loginForm');
    const helpText = document.querySelector('.text-center p');

    loginForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const login = document.getElementById('login').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!login) {
            alert('Login cannot be empty.');
            return;
        }

        // Walidacja hasła
        if (!password) {
            alert('Password cannot be empty.');
            return;
        }

        // Wysyłanie danych na serwer
        sendSessionDataToServer(login, password);
    });

    // Funkcja wysyłania danych na serwer
    function sendSessionDataToServer(login, password) {
        const sessionData = {
            login: login,
            password: password
        };

        console.log('Sending data to server:', sessionData);

        fetch('http://localhost:8080/api/sessions/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(sessionData)
        })
            .then(response => {
                if (response.ok) {
                    return response.json(); // Oczekujemy odpowiedzi w formacie JSON przy sukcesie
                } else {
                    return response.text().then(text => { throw new Error(text); }); // Obsługujemy tekst przy błędzie
                }
            })
            .then(data => {
                localStorage.setItem('userId', data.id);
                if (data.role === 'ADMIN') {
                    window.location.href = '/main_admin';
                } else if (data.role === 'LECTURER') {
                    window.location.href = '/main_lecturer';
                } else if (data.role === 'STUDENT') {
                    window.location.href = '/main_student';
                } else {
                    alert('Unknown role received from server.');
                }
            })
            .catch(error => {
                console.error('Error during login:', error);
                // Zmiana tekstu "Need help?" na czerwony komunikat o błędzie
                helpText.textContent = 'Credentials not correct';
                helpText.style.color = 'red';
            });
    }
});
