document.addEventListener('DOMContentLoaded', async () => {
    const sessionToken = localStorage.getItem('sessionToken'); // Pobranie tokena z LocalStorage

    if (sessionToken) {
        try {
            const response = await fetch(`/api/sessions/check?sessionToken=${sessionToken}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
            });

            if (response.ok) {
                const data = await response.json();
                console.log("Session check response:", data);

                // Jeśli sesja jest aktywna, przekieruj użytkownika na odpowiednią stronę
                if (data.active) {
                    if (data.role === 'STUDENT') {
                        window.location.href = '/main_student';
                    } else if (data.role === 'LECTURER') {
                        window.location.href = '/main_lecturer';
                    } else if (data.role === 'ADMIN') {
                        window.location.href = '/main_admin';
                    } else {
                        console.error("Unknown role received:", data.role);
                    }
                }
            } else if (response.status === 401) {
                // Jeśli sesja nie jest aktywna (401 - Unauthorized)
                console.log("Session is not active. User can log in.");
            } else {
                console.error("Unexpected error while checking session:", await response.text());
            }
        } catch (error) {
            console.error("Error checking session:", error.message);
        }
    } else {
        console.log("No session token found. User can log in.");
    }
});
