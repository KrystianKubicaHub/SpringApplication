document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('.large-btn');
    const displayPanel = document.querySelector('.display-panel');


    function handleButtonClick(event) {
        const clickedButtonIfItWasLargeBtn = event.target.closest('.large-btn');
        const clickedButtonIfItWasCancelBtn = event.target.closest('.cancel-btn');



        if (clickedButtonIfItWasLargeBtn) {
            resetButtonStyles();
            clickedButtonIfItWasLargeBtn.classList.remove(clickedButtonIfItWasLargeBtn.dataset.originalClass);
            clickedButtonIfItWasLargeBtn.classList.remove('large-btn');
            clickedButtonIfItWasLargeBtn.classList.add('cancel-btn');
            clickedButtonIfItWasLargeBtn.textContent = "Cancel";
            moveButtonsToTop();
            const buttonId = clickedButtonIfItWasLargeBtn.id;
            switch (buttonId) {
                case 'add-person-btn':
                    addPersonClicked();
                    break;
                case 'show-people-btn':
                    showPeopleClicked();
                    break;
                case 'academy-details-btn':
                    academyDetailsClicked().then(result => {
                        if (result) {
                            resetButtonStyles();
                            resetButtonsPosition();
                            resetContentPanel();
                        }
                    }).catch(error => {
                        console.error('An error occurred while processing academy details:', error);
                    });
                    break;
                case 'show-courses-btn':
                    handleShowCourses();
                    break;
                case 'add-course-btn':
                    handleAddCourse();
                    break;
                default:
                    console.warn('Unknown button clicked:', buttonId);
                    break;
            }
            displayPanel.classList.add('panel-expanded');
            displayPanel.classList.remove('display-panel');
            displayPanel.classList.remove('panel-collapsed');
        }
        if (clickedButtonIfItWasCancelBtn) {
            resetButtonStyles();
            resetButtonsPosition();
            resetContentPanel();
        }
    }



    buttons.forEach(button => {
        button.dataset.originalText = button.textContent;
        button.dataset.originalClass = [...button.classList].find(cls => cls !== 'large-btn');
        button.addEventListener('click', handleButtonClick);
    });
});



function resetContentPanel() {
    const displayPanel = document.querySelector('.panel-expanded');

    displayPanel.classList.remove('panel-expanded');
    displayPanel.classList.add('panel-collapsed');

    displayPanel.innerHTML = '';

    const header = document.createElement('h2');
    header.textContent = 'SELECT OPERATION';
    displayPanel.prepend(header);
}

function resetButtonStyles() {
    const buttons = document.querySelectorAll('.cancel-btn');
    buttons.forEach(button => {
        button.classList.remove('cancel-btn');
        button.classList.add('large-btn');
        button.textContent = button.dataset.originalText;
    });
}
function moveButtonsToTop() {
    const buttonsContainer = document.querySelector('.buttons-container');
    buttonsContainer.classList.add('fixed');
}
function resetButtonsPosition() {
    const buttonsContainer = document.querySelector('.buttons-container');
    buttonsContainer.classList.remove('fixed');
}




