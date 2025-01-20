async function checkSession() {
    const sessionToken = localStorage.getItem('sessionToken');

    if (!sessionToken) {
        console.error("No session token found. Redirecting to login page...");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await fetch(`/api/sessions/check?sessionToken=${sessionToken}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        if (response.ok) {
            const data = await response.json();
            console.log("Session is active:", data);

            const currentPath = window.location.pathname; // Pobierz aktualny URL

            if (data.role === "STUDENT" && currentPath !== "/main_student") {
                console.log("Redirecting to the correct page for STUDENT...");
                window.location.href = "/main_student";
            } else if (data.role === "LECTURER" && currentPath !== "/main_lecturer") {
                console.log("Redirecting to the correct page for LECTURER...");
                window.location.href = "/main_lecturer";
            } else if (data.role === "ADMIN" && currentPath !== "/main_admin") {
                console.log("Redirecting to the correct page for ADMIN...");
                window.location.href = "/main_admin";
            } else if (data.role) {
                console.log(`User is already on the correct page for role: ${data.role}`);
            } else {
                console.error("Unknown role or no redirection required:", data.role);
            }

        } else {
            console.error("Session is not active. Redirecting to login page");
            window.location.href = "/login";
        }
    } catch (error) {
        console.error("Error checking session:", error);
        window.location.href = "/login";
    }
}

async function signOut() {
    const sessionToken = localStorage.getItem('sessionToken');

    if (!sessionToken) {
        console.error('Session token is missing. Unable to log out.');
        return;
    }

    try {
        const response = await fetch(`/api/sessions/logout?sessionToken=${sessionToken}`, {
            method: 'POST',
        });

        if (!response.ok) {
            const responseText = await response.text();
            console.error('Server response:', responseText);
            throw new Error('Failed to log out.');
        }

        console.log('User logged out successfully.');
        localStorage.removeItem('sessionToken');
        window.location.href = '/login';
    } catch (error) {
        console.error('Error during logout:', error.message);
    }
}


document.addEventListener('DOMContentLoaded', () => {
    const signOutButton = document.querySelector('.sign-out-btn');
    signOutButton.addEventListener('click', signOut);
});


checkSession();
