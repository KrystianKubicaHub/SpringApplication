async function fetchStudentData(userId) {
    try {
        const response = await fetch(`/api/student/data?userId=${userId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });

        if (!response.ok) throw new Error(`Error fetching student data. Status: ${response.status}`);

        const studentData = await response.json();
        updateView(studentData, userId);
    } catch (error) {
        console.error('Error fetching student data:', error.message);
    }
}

async function updateStudentData(userId, student) {
    try {
        const response = await fetch(`/api/student/update?userId=${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student),
        });

        if (!response.ok) throw new Error(`Error updating student data. Status: ${response.status}`);

        console.log('Student data updated successfully.');
    } catch (error) {
        console.error('Error updating student data:', error.message);
    }
}

async function removeEnrollmentById(userId, enrollmentId) {
    try {
        if (userId == null || enrollmentId == null) {
            const missingIds = [];
            if (userId == null) {
                missingIds.push('User ID');
            }
            if (enrollmentId == null) {
                missingIds.push('Enrollment ID');
            }
            console.error(`${missingIds.join(' and ')} is missing.`);
            return;
        }


        const response = await fetch(`/api/enrollment/remove?userId=${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: enrollmentId, // Przekazywanie liczby bez JSON.stringify
        });

        if (!response.ok) {
            const responseText = await response.text();
            console.error('Server response:', responseText);
            throw new Error(responseText);
        }

        console.log(`Enrollment with ID ${enrollmentId} removed successfully.`);
    } catch (error) {
        console.error('Error removing enrollment:', error.message);
    }
}





async function registerForCourse(userId, courseId) {
    try {
        const response = await fetch(`/api/courses/register?userId=${userId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: courseId,
        });

        if (!response.ok) throw new Error(`Error registering for course. Status: ${response.status}`);

        console.log('Registered for course successfully.');
    } catch (error) {
        console.error('Error registering for course:', error.message);
    }
}

async function fetchAndDisplayCourses(userId) {
    try {
        const coursePanel = document.querySelector('.courses-panel');
        if (!coursePanel) {
            console.error('Courses panel not found');
            return;
        }

        const response = await fetch(`/api/courses?userId=${userId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        });

        if (!response.ok) throw new Error(`Error fetching courses. Status: ${response.status}`);

        const courses = await response.json();

        // Czyszczenie panelu
        coursePanel.innerHTML = '<h2 style="color: #4CAF50; text-align: center; border-bottom: 2px solid #4CAF50; padding-bottom: 10px;">Available Courses</h2>';

        if (!courses.length) {
            const noCoursesMessage = document.createElement('p');
            noCoursesMessage.textContent = 'No courses available at the moment.';
            noCoursesMessage.style.textAlign = 'center';
            noCoursesMessage.style.color = '#FF5722';
            noCoursesMessage.style.fontSize = '18px';
            noCoursesMessage.style.fontWeight = 'bold';
            coursePanel.appendChild(noCoursesMessage);
            return;
        }

        const courseList = document.createElement('ul');
        courseList.style.listStyle = 'none';
        courseList.style.padding = '0';

        courses.forEach(course => {
            const courseItem = document.createElement('li');
            courseItem.style.border = '1px solid #ddd';
            courseItem.style.borderRadius = '8px';
            courseItem.style.margin = '15px 0';
            courseItem.style.padding = '15px';
            courseItem.style.backgroundColor = '#ffffff';
            courseItem.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
            courseItem.style.transition = 'transform 0.2s, box-shadow 0.2s';

            courseItem.addEventListener('mouseover', () => {
                courseItem.style.transform = 'scale(1.02)';
                courseItem.style.boxShadow = '0 6px 10px rgba(0, 0, 0, 0.2)';
            });

            courseItem.addEventListener('mouseout', () => {
                courseItem.style.transform = 'scale(1)';
                courseItem.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
            });

            const courseName = document.createElement('h3');
            courseName.textContent = course.name;
            courseName.style.margin = '0 0 10px';
            courseName.style.color = '#2196F3';

            const courseDescription = document.createElement('p');
            courseDescription.textContent = course.description;
            courseDescription.style.color = '#757575';

            const ectsInfo = document.createElement('p');
            ectsInfo.textContent = `ECTS: ${course.ectsCredits}`;
            ectsInfo.style.fontWeight = 'bold';
            ectsInfo.style.color = '#009688';

            const enrollButton = document.createElement('button');
            enrollButton.textContent = 'Enroll';
            enrollButton.style.padding = '8px 16px';
            enrollButton.style.marginTop = '10px';
            enrollButton.style.border = 'none';
            enrollButton.style.borderRadius = '4px';
            enrollButton.style.backgroundColor = '#4CAF50';
            enrollButton.style.color = '#fff';
            enrollButton.style.fontSize = '14px';
            enrollButton.style.cursor = 'pointer';

            enrollButton.addEventListener('mouseover', () => {
                enrollButton.style.backgroundColor = '#45a049';
            });

            enrollButton.addEventListener('mouseout', () => {
                enrollButton.style.backgroundColor = '#4CAF50';
            });

            enrollButton.onclick = async () => {
                await registerForCourse(userId, course.id);
                await fetchAndDisplayCourses(userId);
            };

            courseItem.appendChild(courseName);
            courseItem.appendChild(courseDescription);
            courseItem.appendChild(ectsInfo);
            courseItem.appendChild(enrollButton);
            courseList.appendChild(courseItem);
        });

        coursePanel.appendChild(courseList);
    } catch (error) {
        console.error('Error fetching courses:', error.message);
    }
}

